package com.hbc.pms.core.api.support.notification;

import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.enums.AlarmActionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailChannel implements Channel {
  @Override
  public void notify(AlarmAction action, AlarmCondition condition) {
    if (!action.getType().equals(AlarmActionType.EMAIL)) {
      return;
    }
    var emails = action.getRecipients();
    log.info(condition.getId() + " - Sending email to " + emails);
  }
}
