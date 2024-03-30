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

    switch (queryType) {
      case DAY -> {
        return formatDate(start, "dd/MM");
      }
      case WEEK, MONTH -> {
        var pattern = "dd/MM";
        return formatDate(start, pattern) + " - " + formatDate(end, pattern);
      }
      case YEAR -> {
        var pattern = "dd/MM/yy";
        return formatDate(start, pattern) + " - " + formatDate(end, pattern);
      }
    }

    return "";
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
