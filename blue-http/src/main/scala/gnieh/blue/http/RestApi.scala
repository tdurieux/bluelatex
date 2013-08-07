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
package http

import tiscaf.{
  HLet,
  HReqData
}

import org.osgi.framework.BundleContext

import scala.collection.mutable.ListBuffer

import java.text.{ SimpleDateFormat, ParseException }

/** The rest interface may be extended by \BlueLaTeX modules.
 *  Theses module simply need to register services implementing this trait
 *  to make the new interface available.
 *
 *  '''Note''': make sure that the interface in your module does not collide
 *  with another existing and already registered module. In such a case, the first
 *  service found in the list will be taken, which order is not possible to predict
 *
 *  @author Lucas Satabin
 */
trait RestApi {

  private[http] val posts = ListBuffer.empty[PartialFunction[HReqData, HLet]]
  private[http] val gets = ListBuffer.empty[PartialFunction[HReqData, HLet]]
  private[http] val deletes = ListBuffer.empty[PartialFunction[HReqData, HLet]]

  def POST(handler: PartialFunction[HReqData, HLet]) {
    posts += handler
  }

  def GET(handler: PartialFunction[HReqData, HLet]) {
    gets += handler
  }

  def DELETE(handler: PartialFunction[HReqData, HLet]) {
    deletes += handler
  }

  // ======== Some useful extractors ========

  object dot {
    def unapply(input: String) = {
      val index = input.lastIndexOf('.')
      if (index > 0) {
        // there is at least tow elements
        Some((input.substring(0, index), input.substring(index + 1)))
      } else {
        None
      }
    }
  }

  object long {
    def unapply(input: String) = try {
      Some(input.toLong)
    } catch {
      case _: Exception => None
    }
  }

  object int {
    def unapply(input: String) = try {
      Some(input.toInt)
    } catch {
      case _: Exception => None
    }
  }

  object date {
    val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    def unapply(input: String) = try {
      Option(formatter.parse(input))
    } catch {
      case _: ParseException =>
        None
    }
  }

  /** Specific extractor to extract the path and query parts from a request */
  object ? {

    def unapply(req: HReqData): Option[(String, String)] =
      if(req.query != "")
        Some(req.uriPath, req.query)
      else
        None

  }

  /** Enrich `StringContext` with string enterpolators used to pattern match against q request */
  implicit class RestContext(val sc: StringContext) {

    /** Allows people to pattern match against some URL and bind values when needed */
    object path {

      val regex = sc.parts.map(scala.util.matching.Regex.quoteReplacement).mkString("([^/]+)").r

      def unapplySeq(s: String): Option[Seq[String]] =
        regex.unapplySeq(s)

      def unapplySeq(req: HReqData): Option[Seq[String]] =
        regex.unapplySeq(req.uriPath)

    }

    /** Allows people to pattern match agsinast some query string */
    object query {

      val regex = sc.parts.map(scala.util.matching.Regex.quoteReplacement).mkString("([^&]+)").r

      def unapplySeq(s: String): Option[Seq[String]] =
        regex.unapplySeq(s)

    }

  }

}
