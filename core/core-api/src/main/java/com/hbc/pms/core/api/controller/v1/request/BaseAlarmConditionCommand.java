package com.hbc.pms.core.api.controller.v1.request;

import com.hbc.pms.core.model.enums.AlarmSeverity;
import com.hbc.pms.core.model.enums.AlarmType;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class BaseAlarmConditionCommand {
  private Boolean isEnabled = true;
  private AlarmSeverity severity;
  private AlarmType type;

  @Range(min = 1, max = 3600, message = "Check Interval must be from 1 to 3600 seconds")
  private int checkInterval;

  @Range(min = 1, max = 3600, message = "Time Delay must be from 1 to 3600 seconds")
  private int timeDelay;

  private Double min;
  private Double max;

  private boolean isCustomAlarmType() {
    return type == AlarmType.CUSTOM;
  }

  @AssertFalse(message = "Both min & max can not be null for CUSTOM alarm type")
  private boolean isBothMinMaxNull() {
    if (isCustomAlarmType()) {
      return min == null && max == null;
    }

    return false;
  }

  @AssertTrue(message = "Min must be smaller than Max for CUSTOM alarm type")
  private boolean isValidMinMax() {
    if (isCustomAlarmType() && min != null && max != null) {
      return min < max;
    }

    return true;
  }
}
