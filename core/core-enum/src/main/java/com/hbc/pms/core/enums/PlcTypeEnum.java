package com.hbc.pms.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlcTypeEnum {
  BOOL("Bool"),
  REAL("Real"),
  DINT("Dint");

  private final String name;
}
