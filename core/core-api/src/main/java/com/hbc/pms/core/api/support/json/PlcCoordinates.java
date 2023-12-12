package com.hbc.pms.core.api.support.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlcCoordinates {
  @JsonProperty("isConnected")
  private Coordinate isConnected;

  @JsonProperty("temperature")
  private Coordinate temperature;

  @JsonProperty("voltage")
  private Coordinate voltage;
}
