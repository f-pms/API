package com.hbc.pms.core.api.controller.v1.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OneDayChartResponse {
  private OffsetDateTime recordingDate;
  private List<Map<String, Double>> data;
}
