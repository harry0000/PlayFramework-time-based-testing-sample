package controllers

import javax.inject._

import play.api.Configuration
import play.api.mvc._
import play.twirl.api.Html
import utils.TimeUtil

@Singleton
class Application @Inject() (val timeUtil: TimeUtil,
                             val configuration: Configuration) extends Controller with MaintenanceController with MaintenancePage with MaintenanceApi {

  def maintenancePage: Html = views.html.maintenance("Under maintenance.")

  def index = MaintenancePageAction { Action { implicit request =>
    Ok(views.html.index("Your new application is ready."))
  }}

  def api = MaintenanceApiAction { Action { implicit request =>
    Ok
  }}

}
