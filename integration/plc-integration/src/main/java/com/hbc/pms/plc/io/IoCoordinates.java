package com.hbc.pms.plc.io;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class IoCoordinates {
  private String id;
  private PmsCoordinate pmsCoordinate;

  @JsonIgnoreProperties(ignoreUnknown = true)
  @Getter
  @Setter
  public static class PmsCoordinate {
    @JsonProperty("isConnected")
    private Coordinate isConnected;

    @JsonProperty("temperature")
    private Coordinate temperature;

    @JsonProperty("voltage")
    private Coordinate voltage;
  }
}
