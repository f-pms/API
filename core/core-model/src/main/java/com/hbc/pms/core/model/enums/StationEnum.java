package com.hbc.pms.core.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StationEnum {
  MAIN("main"),
  TR11("tr11"),
  TR12("tr12"),
  TR30("tr30"),
  TR31("tr31"),
  TR32("tr32"),
  TR33("tr33"),
  TR34("tr34"),
  TR42("tr42");

  private final String name;

}
