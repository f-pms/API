package com.hbc.pms.core.api.util

import spock.lang.Specification

import java.time.OffsetDateTime
import java.time.ZoneOffset


class CronUtilTest extends Specification {
  def "Assert #expression - #expectedResult"() {
    given:
    var time = OffsetDateTime.of(2001, 1, 1, 0, 1, 30, 0, ZoneOffset.UTC)

    when:
    boolean isMatch = CronUtil.matchTime(cron, time)

    then:
    isMatch == expectedResult

    where:
    cron               | expectedResult | expression
    "*/30 */1 * * * *" | true           | "every 1 minute 30 seconds"
    "*/90 * * * * *"   | false          | "every 90 seconds"
    "* */1.5 * * * *"  | false          | "every 1 minute 30 seconds"
  }

  def "Assert #minute-#second (minute-second) - #expectedResult"() {
    given:
    def cron = "0 0-5 6 * * *"
    def time = OffsetDateTime.of(2001, 1, 1, 6, minute, second, 0, ZoneOffset.ofHours(7))

    when:
    boolean isMatch = CronUtil.matchTime(cron, time)

    then:
    isMatch == expectedResult

    where:
    minute | second | expectedResult
    0      | 0      | true
    1      | 0      | true
    2      | 0      | true
    3      | 0      | true
    4      | 0      | true
    5      | 0      | true
    0      | 1      | false
    0      | 2      | false
    1      | 1      | false
    6      | 0      | false
    7      | 2      | false
  }
}
