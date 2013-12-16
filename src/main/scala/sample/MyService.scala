package sample

import akka.actor.{Props, ActorSystem, Actor, ActorLogging}
import spray.can.Http
import spray.http._
import HttpMethods._
import scala.concurrent.duration._
import spray.http.HttpRequest
import spray.http.HttpResponse

case class Slowness(howSlow: FiniteDuration)

class SlowOne extends Actor {
  import context.dispatcher
  def receive = {
    case Slowness(magnitude) => {
      val s = sender
      context.system.scheduler.scheduleOnce(magnitude) {
        s !  HttpResponse(entity ="slow one done " + System.currentTimeMillis)
      }
    }
  }
}

class PosterChild extends Actor with  ActorLogging {
  def receive = {
    case body:String => {
      log.info(s"Body posted: $body")
      sender !  HttpResponse(entity ="poster child done ")
    }
  }
}

class MyService extends Actor with  ActorLogging {

  implicit val system = ActorSystem()
  val slowOne = system.actorOf(Props[SlowOne])
  val posterChild = system.actorOf(Props[PosterChild])

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

    case HttpRequest(POST, Uri.Path("/poster"),_,entity,_) => {
      posterChild forward entity.asString
    }

    case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
      sender ! HttpResponse(entity = "PONG!")

    case HttpRequest(GET, Uri.Path(path),_,_,_) if path startsWith "/slowone" => {
      val timeout = parseTimeout(path)
      slowOne forward Slowness(timeout)
    }

    case _: HttpRequest => sender ! HttpResponse(status = 404, entity = "Unknown resource!")

    case Timedout(HttpRequest(method, uri, _, _, _)) =>
      sender ! HttpResponse(
        status = 500,
        entity = "The " + method + " request to '" + uri + "' has timed out..."
      )

  }
}
