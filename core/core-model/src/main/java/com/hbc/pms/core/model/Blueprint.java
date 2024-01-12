package com.hbc.pms.core.model;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Blueprint {
  private Long id;
  private String name;
  private String description;
  private List<SensorConfiguration> sensorConfigurations;

  @Data
  public static class SensorConfiguration {
    private Long id;
    private String name;
    private List<Figure> figures;
  }

  @Data
  public static class Figure {
    private Long id;
    private Point displayCoordinate;
    private String address;
  }

  @Data
  public static class Point {
    private double x;
    private double y;
  }

  public List<String> getAddresses() {
    return sensorConfigurations.stream()
        .flatMap(sensorConfiguration -> sensorConfiguration.figures.stream())
        .map(Figure::getAddress)
        .collect(Collectors.toList());
  }
}
