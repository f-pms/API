package com.hbc.pms.core.api.constant;

import java.util.List;
import java.util.Map;

public class ChartConstant {
  // TODO: find way to avoid duplicate
  public static final Map<Long, List<String>> REPORT_TYPE_TO_KEYS =
      Map.of(
          1L,
          List.of(
              "SUM_PEAK",
              "SUM_OFFPEAK",
              "SUM_STANDARD",
              "SUM_TOTAL",
              "SUM_SPECIFIC_1",
              "SUM_SPECIFIC_2",
              "SUM_SPECIFIC_3"),
          2L,
          List.of(
              "SUM_PEAK",
              "SUM_OFFPEAK",
              "SUM_STANDARD",
              "SUM_TOTAL",
              "SUM_SPECIFIC_1",
              "SUM_SPECIFIC_2",
              "SUM_SPECIFIC_3",
              "SUM_SPECIFIC_4",
              "SUM_SPECIFIC_5",
              "SUM_SPECIFIC_6",
              "SUM_SPECIFIC_7",
              "SUM_SPECIFIC_8",
              "SUM_SPECIFIC_9",
              "SUM_SPECIFIC_11",
              "SUM_SPECIFIC_12",
              "SUM_SPECIFIC_13",
              "SUM_SPECIFIC_14",
              "SUM_SPECIFIC_15",
              "SUM_SPECIFIC_16"));
}
