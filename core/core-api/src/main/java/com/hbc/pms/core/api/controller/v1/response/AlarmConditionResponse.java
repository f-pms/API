package com.hbc.pms.core.api.controller.v1.response;

import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.SensorConfiguration;
import com.hbc.pms.core.model.enums.AlarmSeverity;
import com.hbc.pms.core.model.enums.AlarmType;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AlarmConditionResponse {

  private Long id;
  private boolean isEnabled;
  private AlarmSeverity severity;
  private SensorConfiguration sensorConfiguration;

  private AlarmType type;

  // predefined condition
  private String cron;
  private int timeDelay;
  private Double min;
  private Double max;

  private BlueprintForConditionResponse blueprint;
  private List<AlarmAction> actions;

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

  @Data
  public static class BlueprintForConditionResponse {
    private Long id;
    private String name;
    private String description;
  }
}
