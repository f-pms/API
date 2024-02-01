package com.hbc.pms.core.api.controller.v1.request;

import com.hbc.pms.core.model.enums.AlarmActionType;
import com.hbc.pms.core.model.enums.AlarmSeverity;
import com.hbc.pms.core.model.enums.AlarmType;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class CreateAlarmConditionCommand {
  private Boolean isEnabled = true;
  private AlarmSeverity severity;
  private Long sensorConfigurationId;
  private AlarmType type;

  @Min(value = 1, message = "checkInterval must be greater than 0")
  private int checkInterval;

  @Min(value = 1, message = "timeDelay must be greater than 0")
  private int timeDelay;
  private Double min;
  private Double max;
  private String message;
  private List<CreateAlarmActionCommand> actions;

  @Data
  public static class CreateAlarmActionCommand {
    private AlarmActionType type;
    private Set<String> recipientIds;
  }
}
