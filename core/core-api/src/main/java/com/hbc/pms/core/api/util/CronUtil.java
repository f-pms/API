package com.hbc.pms.core.api.util;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import lombok.experimental.UtilityClass;
import org.springframework.scheduling.support.CronExpression;

@UtilityClass
public class CronUtil {

  public static boolean matchTime(String cron, OffsetDateTime time) {
    if (!CronExpression.isValidExpression(cron)) {
      return false;
    }

    var cronExp = CronExpression.parse(cron);
    var truncatedTime = time.truncatedTo(ChronoUnit.SECONDS);
    var truncatedExpressionOffset = truncatedTime.minusNanos(1L);
    var nextExpression = cronExp.next(truncatedExpressionOffset);
    return truncatedTime.equals(nextExpression);
  }

  public static boolean matchCurrentTime(String cron) {
    return matchTime(cron, OffsetDateTime.now());
  }
}
