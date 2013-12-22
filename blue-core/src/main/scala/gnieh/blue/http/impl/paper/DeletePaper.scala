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
package impl
package paper

import couch._

import gnieh.sohva.UserInfo

import java.util.UUID
import java.io.{
  File,
  FileWriter
}

import tiscaf._

import com.typesafe.config.Config

import resource._

import scala.sys.process._

import scala.util.{
  Try,
  Success
}

/** Delete an existing paper.
 *
 *  @author Lucas Satabin
 */
class DeletePaperLet(paperId: String, config: Config, recaptcha: ReCaptcha) extends RoleLet(paperId, config) {

  def roleAct(user: UserInfo, role: PaperRole)(implicit talk: HTalk): Try[Unit] = role match {
    case Author =>
      // only authors may delete a paper
      // first delete the paper files
      import FileProcessing._
      val dirDeleted = configuration.paperDir(paperId).deleteRecursive()
      if(dirDeleted) {
        database("blue_papers").deleteDoc(paperId) map {
          case true =>
            talk.writeJson(true)
          case false =>
            talk
              .writeJson(ErrorResponse("cannot_delete_paper", "Unable to delete the paper database"))
              .setStatus(HStatus.InternalServerError)
        }
      } else {
        Success(talk
          .writeJson(ErrorResponse("cannot_delete_paper", "Unable to delete the paper files"))
          .setStatus(HStatus.InternalServerError))
      }

    case _ =>
      Success(talk.writeJson(ErrorResponse("no_sufficient_rights", "Only authors may delete a paper"))
        .setStatus(HStatus.Forbidden))

  }

}
