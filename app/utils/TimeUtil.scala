package utils

import java.time._
import java.time.temporal.ChronoField
import javax.inject._

trait ClockProvider {
  def clock: Clock
  def now: Instant = Instant.now(clock)
}

object SystemClockProvider extends ClockProvider {
  def clock: Clock = Clock.systemUTC()
}

object TestClockProvider extends ClockProvider {
  var clock: Clock = Clock.systemUTC()

  def fixed(zonedDateTime: ZonedDateTime) = {
    clock = Clock.fixed(zonedDateTime.toInstant, zonedDateTime.getOffset)
  }

  def fixed(offsetTime: OffsetTime) = {
    val offset = offsetTime.getOffset
    clock = Clock.fixed(offsetTime.atDate(LocalDate.now(offset)).toInstant, offset)
  }

  def plusMillis(millisToAdd: Long): Unit = {
    clock = Clock.fixed(clock.instant().plusMillis(millisToAdd), clock.getZone)
  }

  def reset(): Unit = { clock = Clock.systemUTC() }
}

sealed trait Offset {
  protected def offset: ZoneOffset
}

sealed trait UTCOffset extends Offset {
  def offset: ZoneOffset = ZoneOffset.UTC
}

sealed trait JaOffset extends Offset {
  def offset: ZoneOffset = ZoneOffset.ofHours(9)
}

sealed trait Time {
  val toOffsetTime: OffsetTime
  def toMillis: Long = toOffsetTime.getLong(ChronoField.MILLI_OF_DAY)
}

sealed  trait TimeCompanion {
  this: Offset =>

  def now(implicit clockProvider: ClockProvider): OffsetTime = OffsetTime.ofInstant(clockProvider.now, offset)
  def toOffsetTime(epochMilli: Long): OffsetTime = OffsetTime.ofInstant(Instant.ofEpochMilli(epochMilli), offset)
}

case class UTCTime private (toOffsetTime: OffsetTime) extends Time
object UTCTime extends UTCOffset with TimeCompanion {
  def apply()(implicit clockProvider: ClockProvider): UTCTime = UTCTime(now)
  def apply(epochMilli: Long): UTCTime = UTCTime(toOffsetTime(epochMilli))
}

case class JaTime private (toOffsetTime: OffsetTime) extends Time
object JaTime extends JaOffset with TimeCompanion {
  def apply()(implicit clockProvider: ClockProvider): JaTime = JaTime(now)
  def apply(epochMilli: Long): JaTime = JaTime(toOffsetTime(epochMilli))
}
