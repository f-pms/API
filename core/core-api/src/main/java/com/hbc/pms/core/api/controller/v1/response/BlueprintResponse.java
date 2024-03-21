package com.hbc.pms.core.api.controller.v1.response;

import java.util.List;
import lombok.Data;

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
    private double offset;
    private String dataType;
    private double x;
    private double y;

    public void setFields(Object[] resultParts) {
      setDb((Integer) resultParts[0]);
      setOffset((Double) resultParts[1]);
      setDataType((String) resultParts[2]);
    }
  }
}
