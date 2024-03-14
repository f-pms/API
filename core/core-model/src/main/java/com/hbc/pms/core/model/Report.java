package com.hbc.pms.core.model;

import java.time.OffsetDateTime;
import java.util.List;

public class Report {
  private Long id;
  private OffsetDateTime recordingDate;
  private List<ReportRow> rows;
}
