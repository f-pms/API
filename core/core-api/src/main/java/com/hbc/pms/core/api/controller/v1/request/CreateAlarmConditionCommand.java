package com.hbc.pms.core.api.controller.v1.request;

import com.hbc.pms.core.model.enums.AlarmActionType;
import jakarta.validation.constraints.AssertTrue;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateAlarmConditionCommand extends BaseAlarmConditionCommand {
  private Long sensorConfigurationId;

  @Length(max = 255, message = "Max length of the message is 255")
  private String message;

  private List<AlarmActionCommand> actions;

  @Data
  public static class AlarmActionCommand {
    private AlarmActionType type;
    private String message;
    private Set<String> recipients;

    private boolean isEmailType() {
      return type == AlarmActionType.EMAIL;
    }

    @AssertTrue(message = "Set at least 1 recipients for Email")
    private boolean isValidRecipients() {
      if (isEmailType()) {
        return recipients != null && !recipients.isEmpty();
      }
      return true;
    }
  }
}
