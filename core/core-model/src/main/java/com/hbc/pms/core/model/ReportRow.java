package com.hbc.pms.core.model;

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
public class ReportRow {
  private Long id;
  private String indicator;
  private ReportRowShift shift;
  private Double oldElectricValue;
  private Double newElectricValue1;
  private Double newElectricValue2;
  private Double newElectricValue3;
  private Double newElectricValue4;
}
