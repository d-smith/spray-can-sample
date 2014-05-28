package sample

import spray.routing._
import akka.actor.Actor


class RoutedHttpService(route: Route) extends Actor with HttpService {
  implicit def actorRefFactory = context
  def receive = runRoute(route)
}

object RouteConconcatenator extends RouteConcatenation {
  def concatRoutes(chain: Route, routes: List[Route]) : Route = {
    routes match {
      case Nil => chain
      case h :: Nil => chain ~ h
      case h :: t => concatRoutes(chain ~ h, t)
    }
  }
}
