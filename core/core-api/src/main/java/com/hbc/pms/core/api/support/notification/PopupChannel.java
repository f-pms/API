package com.hbc.pms.core.api.support.notification;

import com.hbc.pms.core.api.service.WebSocketService;
import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.AlarmHistory;
import com.hbc.pms.core.model.enums.AlarmActionType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopupChannel extends AbstractChannel {

  private final WebSocketService webSocketService;

  @Override
  protected boolean filter(AlarmAction action) {
    return AlarmActionType.POPUP.equals(action.getType());
  }

  @Override
  protected void send(AlarmHistory history, AlarmCondition condition, AlarmAction action) {
    var alarmData = Map.of(
        "message", action.getMessage(),
        "triggeredAt", history.getTriggeredAt().toString()
    );
    webSocketService.fireAlarm(alarmData);
  }
}
