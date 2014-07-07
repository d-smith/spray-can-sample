package sample

import org.scalatest.WordSpec
import spray.testkit.ScalatestRouteTest
import org.scalatest.matchers.MustMatchers
import spray.routing.{MalformedRequestContentRejection, HttpService}
import spray.http.HttpEntity
import spray.http.ContentTypes._
import spray.httpx.SprayJsonSupport._
import org.junit.runner.RunWith

@RunWith(classOf[org.scalatest.junit.JUnitRunner])
class AddNoteTest extends WordSpec with ScalatestRouteTest with MustMatchers with HttpService {

  def actorRefFactory = system
  val jsonSample = new JsonSample()

  "a note processed by the addnote service" must {
    "be happy when given a good payload" in {
      Post("/addnote", HttpEntity(
        `application/json`, """{"workItemNo":"W00001-01JAN15","memo":"This is a memo", "body":"b-flat"}"""
      )) ~>
        jsonSample.route ~> check {
          import AddNoteResponseJsonSupport._
          val addNoteResponse = responseAs[AddNoteResponse]
          assert(addNoteResponse.workItemNo === "W00001-01JAN15")
          assert(addNoteResponse.timeStamp < System.currentTimeMillis())
      }
    }

    "reject request when given a malformed payload" in {
      Post("/addnote", HttpEntity(
        `application/json`, """{"workItemNo":"W00001-01JAN15","memo":"This is a memo""""
      )) ~>
        jsonSample.route ~> check {
        assert(rejection.getClass === classOf[MalformedRequestContentRejection])
      }
    }
  }
}
