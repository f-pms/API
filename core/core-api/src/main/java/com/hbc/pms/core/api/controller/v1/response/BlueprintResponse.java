package com.hbc.pms.core.api.controller.v1.response;

import lombok.Data;

import java.util.List;

@Data
public class BlueprintResponse {
  private Long id;
  private String name;
  private String description;
  private List<SensorConfigurationResponse> sensorConfigurations;

  @Data
  public static class SensorConfigurationResponse {
    private Long id;
    private String address;
    private int db;
    private int offset;
    private String dataType;
    private double x;
    private double y;

    public void setFields(Object[] resultParts) {
      setDb((Integer) resultParts[0]);
      setOffset((Integer) resultParts[1]);
      setDataType((String) resultParts[2]);
    }
  }
}
