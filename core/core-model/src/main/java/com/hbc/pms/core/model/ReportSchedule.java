package com.hbc.pms.core.model;

import com.hbc.pms.core.model.enums.ReportRowPeriod;
import com.hbc.pms.core.model.enums.ReportRowShift;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ReportSchedule {
  private Long id;
  private String indicator;
  private ReportRowShift shift;
  private ReportRowPeriod period; // indicates which electrical value position needs to be filled in
  private ReportType type;
  private SensorConfiguration sensorConfiguration;
}
