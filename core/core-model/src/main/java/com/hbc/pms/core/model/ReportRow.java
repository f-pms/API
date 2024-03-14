package com.hbc.pms.core.model;

import com.hbc.pms.core.model.enums.ReportRowCategory;

public class ReportRow {
  private Long id;
  private String indicator;
  private ReportRowCategory category;
  private Double oldElectricValue;
  private Double newElectricValue1;
  private Double newElectricValue2;
  private Double newElectricValue3;
  private Double newElectricValue4;
  private Report report;
}
