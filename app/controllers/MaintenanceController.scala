package controllers

import java.time.OffsetTime

import play.api.Configuration
import play.api.mvc._
import play.twirl.api.Html
import utils.TimeUtil

import scala.concurrent.Future

trait MaintenanceController {
  this: Controller =>

  def timeUtil: TimeUtil
  def configuration: Configuration

  def now: OffsetTime = timeUtil.JaTime().toOffsetTime

  def maintenanceStart: OffsetTime = OffsetTime.parse(configuration.getString("maintenance.start").getOrElse("00:00+00:00"))
  def maintenanceEnd:   OffsetTime = OffsetTime.parse(configuration.getString("maintenance.end").getOrElse("00:00+00:00"))

  def isMaintenance: Boolean = {
    if (maintenanceStart.isEqual(maintenanceEnd)) {
      false
    } else {
      val t = now
      (t.isEqual(maintenanceStart) || t.isAfter(maintenanceStart)) && t.isBefore(maintenanceEnd)
    }
  }

  sealed trait MaintenanceAction[A] {
    this: Action[A] =>

    def action: Action[A]
    def result(request: Request[A]): Result

    def apply(request: Request[A]): Future[Result] = {
      if (isMaintenance) {
        Future.successful(result(request))
      } else {
        action(request)
      }
    }

    lazy val parser = action.parser
  }
}

trait MaintenancePage {
  this: Controller with MaintenanceController =>

  def maintenancePage: Html

  case class MaintenancePageAction[A](action: Action[A]) extends Action[A] with MaintenanceAction[A] {
    def result(request: Request[A]): Result = Ok(maintenancePage)
  }
}

trait MaintenanceApi {
  this: Controller with MaintenanceController =>

  def maintenanceResponse: Result = ServiceUnavailable

  case class MaintenanceApiAction[A](action: Action[A]) extends Action[A] with MaintenanceAction[A] {
    def result(request: Request[A]): Result = maintenanceResponse
  }
}
