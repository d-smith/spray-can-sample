package sample

import spray.routing._
import akka.io.IO
import spray.can.Http
import akka.actor.{Actor, Props, ActorSystem}

class PingDirectives extends Directives {
  def route = path("ping") {
    get {
      ctx => {
        ctx.complete("PING!!!")
      }
    }
  }
}

class PongDirectives extends Directives {
  def route = path("pong") {
    get {
      ctx => {
        ctx.complete("PONG!!!")
      }
    }
  }
}

class GnopDirectives extends Directives {
  def route = path("gnop") {
    get {
      ctx => {
        ctx.complete("GNOP!!!")
      }
    }
  }
}



// This main simulates the case where we might find all the directives in an OSGi container
// that have been exported as services, and build a concatenated route to allow a single
// HTTP listener route traffic to the routes in the container.
object Main2 extends App with RouteConcatenation {
  import RouteConconcatenator.concatRoutes

  // We can get a list of exported directives in an OSGi container...
  val routes = List(new PingDirectives().route,
    new PongDirectives().route,
    new GnopDirectives().route)

  // Transform a list of routes into a concatenated route
  val chainedRoutes = concatRoutes(routes.head, routes.tail)

  implicit val system = ActorSystem()
  val rootService = system.actorOf(Props(new RoutedHttpService(chainedRoutes)))
  IO(Http)(system) ! Http.Bind(rootService, "0.0.0.0", port = 8666)
}