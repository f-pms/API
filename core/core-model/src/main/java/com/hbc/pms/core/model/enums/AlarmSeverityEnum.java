package com.hbc.pms.core.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmSeverityEnum {
  URGENT("URGENT"),
  HIGH("HIGH"),
  LOW("LOW");

  private final String value;
}
