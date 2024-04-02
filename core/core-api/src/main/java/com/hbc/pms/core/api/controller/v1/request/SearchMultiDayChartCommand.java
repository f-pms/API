package com.hbc.pms.core.api.controller.v1.request;

import com.hbc.pms.core.api.controller.v1.enums.ChartQueryType;
import com.hbc.pms.core.api.controller.v1.enums.ChartType;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SearchMultiDayChartCommand {
  @NotNull private OffsetDateTime start;

  @NotNull private OffsetDateTime end;

  @Builder.Default
  private ChartQueryType queryType = ChartQueryType.DAY;

  @Builder.Default
  private ChartType chartType = ChartType.PIE;
}
