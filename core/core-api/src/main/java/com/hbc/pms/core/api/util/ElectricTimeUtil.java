package com.hbc.pms.core.api.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ElectricTimeUtil {

  public static final LocalTime SHIFT_1_PERIOD_1_START_TIME = LocalTime.of(6, 0);
  public static final LocalTime SHIFT_1_PERIOD_1_END_TIME = LocalTime.of(9, 29);
  public static final LocalTime SHIFT_1_PERIOD_2_START_TIME = LocalTime.of(9, 30);
  public static final LocalTime SHIFT_1_PERIOD_2_END_TIME = LocalTime.of(11, 29);
  public static final LocalTime SHIFT_1_PERIOD_3_START_TIME = LocalTime.of(11, 30);
  public static final LocalTime SHIFT_1_PERIOD_3_END_TIME = LocalTime.of(16, 59);
  public static final LocalTime SHIFT_1_PERIOD_4_START_TIME = LocalTime.of(17, 0);
  public static final LocalTime SHIFT_1_PERIOD_4_END_TIME = LocalTime.of(17, 59);

  public static final LocalTime SHIFT_2_PERIOD_1_START_TIME = LocalTime.of(18, 0);
  public static final LocalTime SHIFT_2_PERIOD_1_END_TIME = LocalTime.of(19, 59);
  public static final LocalTime SHIFT_2_PERIOD_2_START_TIME = LocalTime.of(20, 0);
  public static final LocalTime SHIFT_2_PERIOD_2_END_TIME = LocalTime.of(21, 59);
  public static final LocalTime SHIFT_2_PERIOD_3_START_TIME = LocalTime.of(22, 0);
  public static final LocalTime SHIFT_2_PERIOD_3_END_TIME = LocalTime.of(3, 59);
  public static final LocalTime SHIFT_2_PERIOD_4_START_TIME = LocalTime.of(4, 0);
  public static final LocalTime SHIFT_2_PERIOD_4_END_TIME = LocalTime.of(5, 59);

  private static final ZoneId VIETNAM_ZONE_ID = ZoneId.of("Asia/Ho_Chi_Minh");

  public static LocalDateTime convertOffsetDateTimeToLocalTime(OffsetDateTime offsetDateTime) {
    ZonedDateTime zoned = offsetDateTime.atZoneSameInstant(VIETNAM_ZONE_ID);
    return zoned.toLocalDateTime();
  }

  public static String getTimeGroup(DayOfWeek dayOfWeek, LocalTime time) {
    if (dayOfWeek == DayOfWeek.SUNDAY) {
      // No peak hours on Sunday
      return isBetween(time, LocalTime.of(4, 0), LocalTime.of(22, 0)) ? "STANDARD" : "OFFPEAK";
    } else {
      // Weekday or Saturday
      if (isBetween(time, LocalTime.of(6, 0), LocalTime.of(9, 30))) {
        return "STANDARD";
      } else if (isBetween(time, LocalTime.of(9, 30), LocalTime.of(11, 30))) {
        return "PEAK";
      } else if (isBetween(time, LocalTime.of(11, 30), LocalTime.of(17, 0))) {
        return "STANDARD";
      } else if (isBetween(time, LocalTime.of(17, 0), LocalTime.of(18, 0))) {
        return "PEAK";
      } else if (isBetween(time, LocalTime.of(18, 0), LocalTime.of(20, 0))) {
        return "PEAK";
      } else if (isBetween(time, LocalTime.of(20, 0), LocalTime.of(22, 0))) {
        return "STANDARD";
      } else {
        return "OFFPEAK";
      }
    }
  }

  private static boolean isBetween(LocalTime time, LocalTime start, LocalTime end) {
    return (time.isAfter(start) || time.equals(start)) && time.isBefore(end);
  }
}
