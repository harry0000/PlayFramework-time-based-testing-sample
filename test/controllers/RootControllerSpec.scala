package controllers

import play.api.test._
import utils.TimeTestSpecification

object RootControllerSpec extends PlaySpecification with TimeTestSpecification {

  "index action" should {
    "render index page" in new WithFixture {
      finishMaintenance()

      val index = route(app, FakeRequest(GET, "/")).get

      status(index) mustEqual OK
      contentType(index) mustEqual Some("text/html")
      contentAsString(index) must contain("Your new application is ready.")
    }

    "render maintenance page when maintenance" in new WithFixture {
      startMaintenance()

      val index = route(app, FakeRequest(GET, "/")).get

      status(index) mustEqual OK
      contentType(index) mustEqual Some("text/html")
      contentAsString(index) must contain("Under maintenance.")
    }
  }

  "api action" should {
    "return OK" in new WithFixture {
      finishMaintenance()

      route(app, FakeRequest(GET, "/api")).map(status) mustEqual Some(OK)
    }

    "return ServiceUnavailable when maintenance" in new WithFixture {
      startMaintenance()

      route(app, FakeRequest(GET, "/api")).map(status) mustEqual Some(SERVICE_UNAVAILABLE)
    }
  }

}
