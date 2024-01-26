package com.hbc.pms.core.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum AlarmStatusEnum {
  TRIGGERED("TRIGGERED"),
  SENT("SENT"),
  SOLVED("SOLVED");

  private final String value;
}
