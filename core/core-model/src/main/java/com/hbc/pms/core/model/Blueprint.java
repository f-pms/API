package com.hbc.pms.core.model;

import com.hbc.pms.core.model.enums.BlueprintType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Blueprint {

  private Long id;
  private String name;
  private String description;
  private BlueprintType type;
  private List<SensorConfiguration> sensorConfigurations;

  public List<String> getAddresses() {
    return sensorConfigurations.stream().map(SensorConfiguration::getAddress).toList();
  }

  public Map<String, List<String>> getAddressToSensorMap() {
    return sensorConfigurations.stream()
        .collect(
            Collectors.groupingBy(
                SensorConfiguration::getAddress,
                Collectors.mapping(sensor -> String.valueOf(sensor.getId()), Collectors.toList())));
  }
}
