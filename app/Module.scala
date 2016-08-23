import play.api._
import play.api.inject.{Binding, Module => PlayModule}
import time._

class Module extends PlayModule {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[ClockProvider].toInstance(SystemClockProvider)
  )

}
