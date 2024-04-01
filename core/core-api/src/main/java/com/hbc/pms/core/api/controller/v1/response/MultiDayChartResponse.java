package com.hbc.pms.core.api.controller.v1.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MultiDayChartResponse {
  private List<String> labelSteps;

  private Map<String, List<OffsetDateTime>> missingDates;

  private Map<String, Map<String, List<Double>>> data;
}
