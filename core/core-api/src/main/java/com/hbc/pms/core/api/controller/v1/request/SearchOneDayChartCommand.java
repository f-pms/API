package com.hbc.pms.core.api.controller.v1.request;

import com.hbc.pms.core.model.ReportType;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class SearchOneDayChartCommand {
  private ReportType reportType;

  private OffsetDateTime date;
}
