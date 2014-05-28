package scala.sample

import org.scalatest.WordSpec
import spray.testkit.ScalatestRouteTest
import org.scalatest.matchers.MustMatchers
import spray.routing.HttpService
import sample.{AddNoteResponseJsonSupport, AddNoteResponse, JsonSample}
import spray.http.HttpEntity
import spray.http.ContentTypes._
import spray.httpx.SprayJsonSupport._


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
  }
}
