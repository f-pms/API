package com.hbc.pms.core.api.controller.v1.request;

import com.hbc.pms.core.model.enums.AlarmActionType;
import com.hbc.pms.core.model.enums.AlarmType;
import jakarta.validation.constraints.AssertTrue;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateAlarmConditionCommand extends BaseAlarmConditionCommand {
  private Long sensorConfigurationId;
  private String message;
  private AlarmType type;
  private List<AlarmActionCommand> actions;

  @Data
  public static class AlarmActionCommand {
    private AlarmActionType type;
    private String message;
    private Set<String> recipientIds;

    private boolean isEmailType() {
      return type == AlarmActionType.EMAIL;
    }

    @AssertTrue(message = "Set at least 1 recipients for Email")
    private boolean isValidRecipients() {
      if (isEmailType()) {
        return recipientIds != null && !recipientIds.isEmpty();
      }
      return true;
    }
  }
}
