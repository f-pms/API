package com.hbc.pms.core.api.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateTimeUtil {
  public static final DateTimeFormatter REPORT_DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static final String VIETNAM_ZONE_STR = "Asia/Ho_Chi_Minh";
  public static final ZoneId VIETNAM_ZONE_ID = ZoneId.of(VIETNAM_ZONE_STR);
  public static final TimeZone VIETNAM_TIMEZONE = TimeZone.getTimeZone(VIETNAM_ZONE_STR);

  public static LocalDateTime convertOffsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
    ZonedDateTime zoned = offsetDateTime.atZoneSameInstant(VIETNAM_ZONE_ID);
    return zoned.toLocalDateTime();
  }
}
