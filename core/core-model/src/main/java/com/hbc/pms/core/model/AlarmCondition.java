package com.hbc.pms.core.model;

import com.hbc.pms.core.model.enums.AlarmSeverity;
import lombok.*;

import java.util.Set;

import static java.util.Objects.nonNull;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AlarmCondition {
  private Long id;
  private String name;
  private String description;
  private boolean isEnabled;
  private AlarmSeverity severity;
  private Set<String> methods;
  private SensorConfiguration sensorConfiguration;

  // TODO: type: PREDEFINED, CUSTOM

  // predefined condition
  private String cron;
  private int timeDelay;
  private Double min;
  private Double max;

  public boolean isMet(Double value) {
    // TODO: separate 2 condition types
    // predefined condition (boolean -> double), min = 0
    // TODO: will implement

    // custom condition
    if (nonNull(min) && nonNull(max)) {
      return (min <= value && value <= max);
    }
    if (nonNull(min)) {
      return (min <= value);
    }
    if (nonNull(max)) {
      return (value <= max);
    }
    return false;
  }
}