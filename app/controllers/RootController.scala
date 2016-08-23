package controllers

import javax.inject._

import play.api.Configuration
import play.api.mvc._
import play.twirl.api.Html
import time.ClockProvider

@Singleton
class RootController @Inject()(val configuration: Configuration,
                               implicit val clockProvider: ClockProvider) extends Controller with MaintenanceController with MaintenancePage with MaintenanceApi {

  def maintenancePage: Html = views.html.maintenance("Under maintenance.")

  def index = MaintenancePageAction { Action { implicit request =>
    Ok(views.html.index("Your new application is ready."))
  }}

  def api = MaintenanceApiAction { Action { implicit request =>
    Ok
  }}

}
