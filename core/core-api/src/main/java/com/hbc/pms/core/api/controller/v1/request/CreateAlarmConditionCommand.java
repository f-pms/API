package com.hbc.pms.core.api.controller.v1.request;

import com.hbc.pms.core.model.enums.AlarmActionType;
import com.hbc.pms.core.model.enums.AlarmSeverity;
import com.hbc.pms.core.model.enums.AlarmType;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateAlarmConditionCommand extends BaseAlarmConditionCommand {
  private Long sensorConfigurationId;
  private String message;
  private AlarmType type;
  private List<CreateAlarmActionCommand> actions;

  @Data
  public static class CreateAlarmActionCommand {
    private AlarmActionType type;
    private Set<String> recipientIds;
  }
}
