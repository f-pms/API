package com.hbc.pms.core.api.util

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import spock.lang.Specification

class DateTimeUtilTest extends Specification {
  def "Assert UTC"() {
    given:
    var time = OffsetDateTime.of(2001, 1, 1, 17, 0, 0, 0, ZoneOffset.UTC)

    when:
    var convertedTime = DateTimeUtil.convertOffsetDateTimeToLocalDateTime(time)

    then:
    convertedTime == LocalDateTime.of(2001, 1, 2, 0, 0, 0)
  }

  def "Assert GTM+7"() {
    given:
    var time = OffsetDateTime.of(2001, 1, 2, 0, 0, 0, 0, ZoneOffset.ofHours(7))

    when:
    var convertedTime = DateTimeUtil.convertOffsetDateTimeToLocalDateTime(time)

    then:
    convertedTime == LocalDateTime.of(2001, 1, 2, 0, 0, 0)
  }
}
