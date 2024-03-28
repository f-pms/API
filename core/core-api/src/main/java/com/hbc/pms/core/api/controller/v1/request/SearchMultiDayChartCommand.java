package com.hbc.pms.core.api.controller.v1.request;

import com.hbc.pms.core.api.controller.v1.enums.ChartQueryType;
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

  @NotNull private ChartQueryType queryType;
}
