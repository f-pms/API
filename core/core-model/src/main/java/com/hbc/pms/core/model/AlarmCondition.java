package com.hbc.pms.core.model;

import static java.util.Objects.nonNull;

import com.hbc.pms.core.model.enums.AlarmSeverity;
import com.hbc.pms.core.model.enums.AlarmType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.support.CronExpression;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AlarmCondition {

  private Long id;
  private boolean isEnabled;
  private AlarmSeverity severity;
  private SensorConfiguration sensorConfiguration;

  // TODO: type: PREDEFINED, CUSTOM
  private AlarmType type;

  // predefined condition
  private String cron;
  private int timeDelay;
  private Double min;
  private Double max;

  private List<AlarmAction> actions;

  public boolean isMet(Double value) {
    // predefined condition
    if (type.equals(AlarmType.PREDEFINED)) {
      return (value < 1);
    }

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

  public int getCheckInterval() {
    String nonNumberPattern = "[^\\d.]";
    String[] parts = cron.split(" ");
    int second = Integer.parseInt(parts[0].replaceAll(nonNumberPattern, ""));
    int minute =
        parts[1].replaceAll(nonNumberPattern, "").isEmpty()
            ? 0
            : Integer.parseInt(parts[1].replaceAll(nonNumberPattern, ""));
    return second + minute * 60;
  }
}
