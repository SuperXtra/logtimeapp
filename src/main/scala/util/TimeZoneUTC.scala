package util

import java.time.{ZoneOffset, ZonedDateTime}

object TimeZoneUTC {
  def currentTime: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)
}
