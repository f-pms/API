package com.hbc.pms.core.model;

import com.hbc.pms.core.model.enums.AlarmSeverityEnum;
import lombok.*;

import static java.util.Objects.nonNull;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AlarmCondition {
  private Long id;
  private String name;
  private String description;
  private String cron;
  private int timeDelay;
  private boolean isEnabled;
  private Double min;
  private Double max;
  private AlarmSeverityEnum severity;
  private SensorConfiguration sensorConfiguration;

  public boolean isMet(Double value) {
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
