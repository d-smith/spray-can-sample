package sample

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import spray.can.Http


object Main extends App {

  implicit val system = ActorSystem()

  val handler = system.actorOf(Props[MyService], name = "handler")
  IO(Http) ! Http.Bind(handler, interface = "localhost", port = 8666)
}
