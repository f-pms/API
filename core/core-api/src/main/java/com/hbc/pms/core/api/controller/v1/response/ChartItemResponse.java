package com.hbc.pms.core.api.controller.v1.response;

import java.util.List;
import lombok.Data;

@Data
public class ChartItemResponse {
  private String label;

  private List<String> dataSet;
}
