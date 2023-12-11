package com.hbc.pms.core.api.support.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Coordinate {
  private String type;

  private int db;

  private int startByte;
}
