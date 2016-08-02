package utils

import java.time.OffsetTime

import org.scalatest.TestData
import org.scalatestplus.play.OneAppPerTest
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

import scala.language.implicitConversions

trait TimeTestSupport extends Fixture {
  this: OneAppPerTest =>

  implicit override def newAppForTest(td: TestData): Application =
    new GuiceApplicationBuilder()
      .configure(
        "maintenance.start" -> "00:00+09:00",
        "maintenance.end"   -> "05:00+09:00"
      )
      .overrides(bind[ClockProvider].toInstance(TestClockProvider))
      .build()

  def fixture(): Unit = {
    TestClockProvider.reset()
  }

  def startMaintenance(): Unit = {
    TestClockProvider.fixed(
      OffsetTime.parse(app.configuration.getString("maintenance.start").get)
    )
  }

  def finishMaintenance(): Unit = {
    TestClockProvider.fixed(
      OffsetTime.parse(app.configuration.getString("maintenance.end").get)
    )
  }

}
