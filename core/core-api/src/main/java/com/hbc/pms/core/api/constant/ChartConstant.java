package com.hbc.pms.core.api.constant;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChartConstant {
  public static final String SUM_PEAK = "SUM_PEAK";
  public static final String SUM_OFFPEAK = "SUM_OFFPEAK";
  public static final String SUM_STANDARD = "SUM_STANDARD";
  public static final String SUM_TOTAL = "SUM_TOTAL";

  public static final List<String> COMMON_INDICATORS =
      List.of(SUM_PEAK, SUM_OFFPEAK, SUM_STANDARD, SUM_TOTAL);
}
