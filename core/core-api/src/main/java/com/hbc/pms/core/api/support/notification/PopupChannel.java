package com.hbc.pms.core.api.support.notification;

import com.hbc.pms.core.api.service.WebSocketService;
import com.hbc.pms.core.model.AlarmCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopupChannel implements Channel {
  private final WebSocketService webSocketService;

  @Override
  public void notify(String method, AlarmCondition condition) {
    if (!"popup".equalsIgnoreCase(method.trim())) {
      return;
    }
    webSocketService.fireAlarm(condition.getId() + " - " + condition.getDescription());
  }
}
