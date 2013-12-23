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
import http._

/** The standard \BlueLaTeX web Api used to serve the web client.
 *
 *  @author Lucas Satabin
 */
class WebApi extends RestApi {

  GET {
    // returns the web client interface
    case p"papers/$paperid/web" =>
      new WebClientLet(paperid)
    // returns some static content
    case req if req.uriPath.startsWith("js/") || req.uriPath.startsWith("css/") || req.uriPath.startsWith("html/") =>
      WebResourceLet
  }

}

