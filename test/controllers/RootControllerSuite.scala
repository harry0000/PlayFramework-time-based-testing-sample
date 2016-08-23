package controllers

import org.scalatestplus.play.{OneAppPerTest, PlaySpec}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TimeTestSupport

class RootControllerSuite extends PlaySpec with OneAppPerTest with TimeTestSupport {

  "index action" should {
    "render index page" in withFixture {
      finishMaintenance()

      val index = route(app, FakeRequest(GET, "/")).get

      status(index) mustBe OK
      contentType(index) mustBe Some("text/html")
      contentAsString(index) must include ("Your new application is ready.")
    }

    "render maintenance page when maintenance" in withFixture {
      startMaintenance()

      val index = route(app, FakeRequest(GET, "/")).get

      status(index) mustBe OK
      contentType(index) mustBe Some("text/html")
      contentAsString(index) must include ("Under maintenance.")
    }
  }

  "api action" should {
    "return OK" in withFixture {
      finishMaintenance()

      route(app, FakeRequest(GET, "/api")).map(status) mustBe Some(OK)
    }

    "return ServiceUnavailable when maintenance" in withFixture {
      startMaintenance()

      route(app, FakeRequest(GET, "/api")).map(status) mustBe Some(SERVICE_UNAVAILABLE)
    }
  }

}
