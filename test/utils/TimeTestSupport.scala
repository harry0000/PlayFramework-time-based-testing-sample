package utils

import java.time.OffsetTime

import org.scalatest.TestData
import org.scalatestplus.play.OneAppPerTest
import org.specs2.execute._
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import time.{ClockProvider, TestClockProvider}

import scala.language.implicitConversions

trait TimeTestSupport {
  def newApp: Application =
    new GuiceApplicationBuilder()
      .configure(
        "maintenance.start" -> "00:00+09:00",
        "maintenance.end"   -> "05:00+09:00"
      )
      .overrides(bind[ClockProvider].toInstance(TestClockProvider))
      .build()

  def startMaintenance()(implicit app: Application): Unit = TestClockProvider.fixed(getOffsetTime("maintenance.start"))

  def finishMaintenance()(implicit app: Application): Unit = TestClockProvider.fixed(getOffsetTime("maintenance.end"))

  def getOffsetTime(key: String)(implicit app: Application): OffsetTime = OffsetTime.parse(app.configuration.getString(key).get)
}

/** for ScalaTest */
trait TimeTestSpec extends TimeTestSupport with Fixture {
  this: OneAppPerTest =>

  implicit override def newAppForTest(td: TestData): Application = newApp

  def fixture(): Unit = {
    TestClockProvider.reset()
  }
}

/** for Specs2 */
trait TimeTestSpecification extends TimeTestSupport {
  abstract class WithFixture extends WithApplication(newApp) {
    override def around[T: AsResult](t: => T): Result = {
      try {
        super.around(t)
      }
      finally {
        TestClockProvider.reset()
      }
    }
  }
}
