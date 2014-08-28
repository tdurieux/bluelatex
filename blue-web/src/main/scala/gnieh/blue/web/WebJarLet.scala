/*
 * This file is part of the \BlueLaTeX project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gnieh.blue
package web

import tiscaf._
import let._

import org.webjars._

import java.util.regex.Pattern;

import org.osgi.framework.BundleContext

import java.io.{
  File,
  PushbackInputStream
}

/** Implements the tiscaf built-in HLet that makes it possible to server
 *  static resources from the class loader.
 *  This implementation overrides de `getResource` method to make it work
 *  in an OSGi container by looking for resrouces in the context of the bundle
 *  class loader
 *
 *  @author Thomas Durieux
 */
class WebJarLet(context: BundleContext, prefix: String, prefixAssets:String) extends ResourceLet {

  lazy val locator = new WebJarAssetLocator(WebJarAssetLocator.getFullPathIndex(Pattern.compile(".*"),
                this.getClass().getClassLoader()));

  System.err.println(locator.getFullPathIndex())
 
  lazy val webjars = locator.getWebJars();

  protected def dirRoot = ""

  override protected def uriRoot = prefix

  override protected def indexes = List("index.html")

  private val webjarPrefix =
    s"${prefix}/${prefixAssets}/"

  override protected def getResource(path: String): java.io.InputStream = {
    val urlWithoutWebJarPath = path.replace(webjarPrefix, "")
 
    val jarName = urlWithoutWebJarPath.substring(0, urlWithoutWebJarPath.indexOf('/'))

    val urlWithoutJarName = urlWithoutWebJarPath.substring(urlWithoutWebJarPath.indexOf('/') + 1)
 
    val jarVersion = webjars.get(jarName)
 
    try {
      val pathAsset = locator.getFullPath(jarName + "/"+ jarVersion + "/" + urlWithoutJarName)

      val url = classOf[WebJarAssetLocator].getClassLoader().getResource(pathAsset)

      if (url == null)
        null
      else url.getProtocol match {
        case "jar" | "bundle"  =>
          val is = new PushbackInputStream(url.openStream)
          try {
            is.available
            val first = is.read()
            if(first == -1) {
              // this is an empty stream representing a directory entry or an empty file
              // we never serve directory entries nor empty files, only files with some content.
              is.close
              null
            } else {
              // unread the first byte and return the input stream
              is.unread(first)
              is
            }
          } catch {
            case _: Exception => null
          }
        case "file" =>
          if(new File(url.toURI).isFile)
            url.openStream
          else
            null
        case _ =>
          null
      }
    } catch {
      case _: Exception => null
    }
  }

}

