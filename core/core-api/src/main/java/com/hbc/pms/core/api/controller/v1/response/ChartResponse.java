package com.hbc.pms.core.api.controller.v1.response;

import com.hbc.pms.core.api.controller.v1.enums.ChartType;
import java.util.List;
import lombok.Data;

@Data
public class ChartResponse {
  private List<String> labelSteps;

  private ChartType type;

  private List<ChartItemResponse> items;
}
