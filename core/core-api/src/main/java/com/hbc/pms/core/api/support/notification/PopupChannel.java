package com.hbc.pms.core.api.support.notification;

import com.hbc.pms.core.api.service.WebSocketService;
import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.enums.AlarmActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopupChannel implements Channel {
  private final WebSocketService webSocketService;

  @Override
  public void notify(AlarmAction action, AlarmCondition condition) {
    if (!action.getType().equals(AlarmActionType.POPUP)) {
      return;
    }
    webSocketService.fireAlarm(condition.getId() + ": " + action.getMessage());
  }
}
