package com.hbc.pms.core.api.controller.v1.request;

import com.hbc.pms.core.api.controller.v1.enums.ChartQueryType;
import com.hbc.pms.core.api.controller.v1.enums.ChartType;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class SearchMultiDayChartCommand {
  @NotNull private OffsetDateTime start;

  @NotNull private OffsetDateTime end;

  private ChartQueryType queryType;

  private ChartType chartType;
}
