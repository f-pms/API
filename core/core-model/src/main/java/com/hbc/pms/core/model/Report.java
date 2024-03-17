package com.hbc.pms.core.model;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Report {
  private Long id;
  private String sumJson;
  private OffsetDateTime recordingDate;
  private ReportType type;
  private List<ReportRow> rows;
}
