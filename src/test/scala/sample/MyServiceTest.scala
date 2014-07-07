package sample

import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import akka.testkit.{ ImplicitSender, TestKit }
import akka.actor.{Props, ActorSystem}
import scala.concurrent.duration._
import spray.http.HttpResponse
import org.junit.runner.RunWith

@RunWith(classOf[org.scalatest.junit.JUnitRunner])
class MyServiceTest(_system: ActorSystem) extends TestKit(_system) with FunSuite with ShouldMatchers with BeforeAndAfterAll with
  ImplicitSender {

  def this() = this(ActorSystem("MyServiceSpec"))

  override def afterAll: Unit = system.shutdown()

  implicit def timeout = 10 seconds

  test("poster child responds with done message") {
    val posterChild = system.actorOf(Props[PosterChild])
    posterChild ! "Test body"
    expectMsg(HttpResponse(entity ="poster child done "))
  }
}
