package com.hbc.pms.core.api.controller.v1.request;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class SearchMultiDayChartCommand {
  // TODO: validation start & end

  @NotNull
  @DateTimeFormat(pattern = "dd-MM-yyyy")
  private Date start;

  @NotNull
  @DateTimeFormat(pattern = "dd-MM-yyyy")
  private Date end;

  @NotNull private QueryType queryType;

  @NotNull private ChartType chartType;

  public enum ChartType {
    PIE,
    MULTI_LINE,
    STACKED_BAR,
  }

  public enum QueryType {
    DAY,
    WEEK,
    MONTH,
    YEAR
  }
}
