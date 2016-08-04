package utils

import java.time._
import java.time.temporal.ChronoField

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

case class UTCTime private (toOffsetTime: OffsetTime) extends Time
object UTCTime extends UTCOffset {
  def apply()(implicit clockProvider: ClockProvider): UTCTime = UTCTime(clockProvider.now)
  def apply(epochMilli: Long): UTCTime = UTCTime(Instant.ofEpochMilli(epochMilli))
  def apply(instant: Instant): UTCTime = UTCTime(OffsetTime.ofInstant(instant, offset))
}

case class JaTime private (toOffsetTime: OffsetTime) extends Time
object JaTime extends JaOffset {
  def apply()(implicit clockProvider: ClockProvider): JaTime = JaTime(clockProvider.now)
  def apply(epochMilli: Long): JaTime = JaTime(Instant.ofEpochMilli(epochMilli))
  def apply(instant: Instant): JaTime = JaTime(OffsetTime.ofInstant(instant, offset))
}
