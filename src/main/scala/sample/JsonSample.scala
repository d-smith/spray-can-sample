package sample

import spray.routing.Directives
import spray.json.DefaultJsonProtocol
import spray.httpx.SprayJsonSupport._
import akka.actor.{Props, ActorSystem}
import akka.io.IO
import spray.can.Http


case class Note(val workItemNo: String, val memo: String, val body: String)

object NoteJsonSupport extends DefaultJsonProtocol {
  implicit val impNote = jsonFormat3(Note)
}

case class AddNoteResponse(val workItemNo: String, val timeStamp: Long)

object AddNoteResponseJsonSupport extends DefaultJsonProtocol {
  implicit val impAddNoteResponse = jsonFormat2(AddNoteResponse)
}

class JsonSample extends Directives {

  def route = path("addnote") {
    import NoteJsonSupport._
    import AddNoteResponseJsonSupport._
    post {
      entity(as[Note]) {
        note =>
          complete(new AddNoteResponse(note.workItemNo, System.currentTimeMillis()))
      }
    }
  }
}

object JsonSampleServer extends App {
  implicit val system = ActorSystem()
  val theRoute = new JsonSample().route
  val rootService = system.actorOf(Props(new RoutedHttpService(theRoute)))
  IO(Http)(system) ! Http.Bind(rootService, "0.0.0.0", port = 8666)
}
