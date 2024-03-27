package com.hbc.pms.core.api.controller.v1.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class OneDayChartResponse {
  @JsonIgnore
  public static final List<String> KEYS =
      List.of(
          "SUM_TOTAL",
          "SUM_PEAK",
          "SUM_OFFPEAK",
          "SUM_STANDARD",
          "SUM_SPECIFIC_1",
          "SUM_SPECIFIC_2",
          "SUM_SPECIFIC_3");

  private List<Map<String, Double>> sumJson;
}
