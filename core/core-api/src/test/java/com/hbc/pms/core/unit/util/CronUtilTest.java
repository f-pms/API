package com.hbc.pms.core.unit.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hbc.pms.core.api.util.CronUtil;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

public class CronUtilTest {

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
}
