package com.hbc.pms.core.unit.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hbc.pms.core.api.util.CronUtil;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class CronUtilTest {

  private static Stream<Arguments> provideIncorrectTimes() {
    return Stream.of(
        Arguments.of(0, 1),
        Arguments.of(0, 2),
        Arguments.of(1, 1),
        Arguments.of(1, 2),
        Arguments.of(6, 0),
        Arguments.of(7, 2));
  }

  @Test
  public void shouldMatchEvery1Minute30Seconds() {
    var cron = "*/30 */1 * * * *";
    var time = OffsetDateTime.of(2001, 1, 1, 0, 1, 30, 0, ZoneOffset.UTC);
    assertTrue(CronUtil.matchTime(cron, time));
  }

  @Test
  public void shouldNotMatchEvery90Seconds() {
    var cron = "*/90 * * * * *";
    var time = OffsetDateTime.of(2001, 1, 1, 0, 1, 30, 0, ZoneOffset.UTC);
    assertFalse(CronUtil.matchTime(cron, time));
  }

  @Test
  public void shouldNotMatchEvery1Minute30Seconds() {
    var cron = "* */1.5 * * * *";
    var time = OffsetDateTime.of(2001, 1, 1, 0, 1, 30, 0, ZoneOffset.UTC);
    assertFalse(CronUtil.matchTime(cron, time));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5})
  public void shouldMatchReportTime(int minute) {
    var cron = "0 0-5 6 * * *";
    var time = OffsetDateTime.of(2001, 1, 1, 6, minute, 0, 0, ZoneOffset.ofHours(7));
    assertTrue(CronUtil.matchTime(cron, time));
  }

  @ParameterizedTest
  @MethodSource("provideIncorrectTimes")
  public void shouldNotMatchReportTime(int minute, int second) {
    var cron = "0 0-5 6 * * *";
    var time = OffsetDateTime.of(2001, 1, 1, 6, minute, second, 0, ZoneOffset.ofHours(7));
    assertFalse(CronUtil.matchTime(cron, time));
  }
}
