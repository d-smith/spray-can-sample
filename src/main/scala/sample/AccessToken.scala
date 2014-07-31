package sample

import spray.routing.Directives
import spray.json.DefaultJsonProtocol
import spray.httpx.SprayJsonSupport._
import akka.actor.{Props, ActorSystem}
import akka.io.IO
import spray.can.Http

case class AccessToken(token: String)

object AccessTokenJsonSupport extends DefaultJsonProtocol {
  implicit val impAddNoteResponse = jsonFormat1(AccessToken)
}

case class AccessTokenParams(grantType: String, clientId: String, clientSecret: String, username: String,
                              password: String) {
  def prettyPrint() : Unit = {
    println(s"grant_type: $grantType, client_id: $clientId, client_secret: $clientSecret, username: $username, password: $password")
  }
}

class AccessTokenRoute extends Directives {
  val route = path("token") {
    import AccessTokenJsonSupport._
    formFields('grant_type, 'client_id, 'client_secret, 'username, 'password).as(AccessTokenParams) { tokenParams:AccessTokenParams =>
        tokenParams.prettyPrint()
        complete(new AccessToken("6CD786AD-AD90-4832-831B-19EEFB80DD98"))
    }
  }
}

object AccessTokenServer extends App {
  implicit val system = ActorSystem()
  val theRoute = new AccessTokenRoute().route
  val rootService = system.actorOf(Props(new RoutedHttpService(theRoute)))
  IO(Http)(system) ! Http.Bind(rootService, "0.0.0.0", port = 8666)
}




