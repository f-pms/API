package com.hbc.pms.core.api.util;

import com.hbc.pms.core.api.controller.v1.enums.ChartQueryType;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
  public static boolean isBetweenDates(
      OffsetDateTime date, OffsetDateTime start, OffsetDateTime end) {
    return (date.isAfter(start) || date.equals(start)) && date.isBefore(end);
  }

  public static String getDateRangeLabel(
      OffsetDateTime start, OffsetDateTime end, ChartQueryType queryType) {
    String pattern =
        switch (queryType) {
          case DAY, WEEK, MONTH -> "dd/MM";
          case YEAR -> "dd/MM/yy";
        };

    String formattedStart = formatDate(start, pattern);
    String formattedEnd = formatDate(end, pattern);

    return formattedStart + " - " + formattedEnd;
  }

  private static String formatDate(OffsetDateTime date, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return date.format(formatter);
  }

  public static OffsetDateTime getNextUpperBoundDate(
      OffsetDateTime date, ChartQueryType queryType) {
    return switch (queryType) {
      case DAY -> date.plusDays(1);
      case WEEK -> date.plusWeeks(1);
      case MONTH -> date.plusMonths(1);
      case YEAR -> date.plusYears(1);
    };
  }
}
