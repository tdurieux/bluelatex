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
package compile
package impl

import org.osgi.framework._
import org.osgi.service.log.LogService

import akka.actor._
import akka.routing.{
  DefaultResizer,
  RoundRobinRouter
}

import common._

import http.RestApi

class CompilationActivator extends BundleActivator {

  self =>

  import OsgiUtils._

  private var dispatcher: Option[ActorRef] = None
  private var commands: Option[ActorRef] = None

  def start(context: BundleContext): Unit = {
    context.trackOne[SynchroServer] {
      case ServiceAdded(synchro) => deploy(context, synchro)
      case ServiceRemoved(_)     => undeploy(context)
    }
  }

  def stop(context: BundleContext): Unit = {
    undeploy(context)
  }

  def deploy(context: BundleContext, synchro: SynchroServer): Unit =
    for {
      loader <- context.get[ConfigurationLoader]
      system <- context.get[ActorSystem]
      logger <- context.get[LogService]
    } {
      val config = loader.load(context.getBundle.getSymbolicName, self.getClass.getClassLoader)

      // create the dispatcher actor
      val disp =
        system.actorOf(
          Props(new CompilationDispatcher(context, synchro, config, logger)),
          name = "compilation-dispatcher"
        )
      dispatcher = Option(disp)
      // create the system command actor
      commands =
        Option(
          system.actorOf(
            Props(new SystemCommandActor(logger)).withRouter(
              new RoundRobinRouter(
                DefaultResizer(
                  config.getInt("tex.min-process"),
                  config.getInt("tex.max-process")
                )
              )
            ),
            name = "system-commands"
          )
        )

      // register the compilation Api
      context.registerService(classOf[RestApi], new CompilationApi(disp, config, logger), null)

    }

  def undeploy(context: BundleContext): Unit = {
    dispatcher.foreach(_ ! PoisonPill)
    commands.foreach(_ ! PoisonPill)
    dispatcher = None
    commands = None
  }

}
