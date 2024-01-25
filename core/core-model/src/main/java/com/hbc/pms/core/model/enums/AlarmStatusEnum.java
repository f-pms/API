package com.hbc.pms.core.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmStatusEnum {
  TRIGGERED("TRIGGERED"),
  SENT("SENT"),
  SOLVED("SOLVED");

  private final String value;
}
