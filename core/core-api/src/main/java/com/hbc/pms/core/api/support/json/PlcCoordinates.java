package com.hbc.pms.core.api.support.json;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PlcCoordinates {

  @JsonAnyGetter
  private final Map<String, Coordinate> properties;

  public PlcCoordinates() {
    properties = new HashMap<>();
  }
}
