package com.hbc.pms.core.model;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Blueprint {
  private Long id;
  private String name;
  private String description;
  private List<SensorConfiguration> sensorConfigurations;

  @Data
  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PACKAGE)
  public static class SensorConfiguration {
    private Long id;
    private String name;
    private List<Figure> sensorConfigurationFigures;
  }

  @Data
  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PACKAGE)
  public static class Figure {
    private Long id;
    private String address;
    private double x;
    private double y;
  }

  public List<String> getAddresses() {
    return sensorConfigurations.stream()
        .flatMap(sensorConfiguration -> sensorConfiguration.sensorConfigurationFigures.stream())
        .map(Figure::getAddress)
        .collect(Collectors.toList());
  }
}
