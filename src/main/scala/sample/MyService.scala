package sample

import akka.actor.{Actor, ActorLogging}
import spray.can.Http
import spray.http._
import HttpMethods._
import scala.concurrent.duration._
import spray.http.HttpRequest
import spray.http.HttpResponse
import spray.http.Uri.Path


class MyService extends Actor with  ActorLogging {
  import context.dispatcher

  def parseTimeout(path: String) : FiniteDuration = {
    val default = 22 seconds
    val split = path.split("/")
    split length match {
      case 0 => default
      case _ => try {
        FiniteDuration(split.reverse.head.toLong, SECONDS)
      } catch {
        case t:Throwable => default
      }
    }

  }

  def receive = {

    case _: Http.Connected => sender ! Http.Register(self)

    case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
      sender ! HttpResponse(entity = "PONG!")

    case HttpRequest(GET, Uri.Path(path),_,_,_) if path startsWith "/slowone" => {
      val timeout = parseTimeout(path)
      val s = sender
      context.system.scheduler.scheduleOnce(timeout) {
        s !  HttpResponse(entity ="slow one done")
      }
    }

    case _: HttpRequest => sender ! HttpResponse(status = 404, entity = "Unknown resource!")

    case Timedout(HttpRequest(method, uri, _, _, _)) =>
      sender ! HttpResponse(
        status = 500,
        entity = "The " + method + " request to '" + uri + "' has timed out..."
      )

  }
}
