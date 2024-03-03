package com.hbc.pms.core.api.event;

import com.hbc.pms.core.api.service.AlarmPersistenceService;
import com.hbc.pms.core.api.service.AlarmService;
import com.hbc.pms.core.api.service.WebSocketService;
import com.hbc.pms.core.model.enums.AlarmStatus;
import com.hbc.pms.plc.api.IoResponse;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AlarmHistoryHandler implements RmsHandler {

  private final AlarmPersistenceService alarmPersistenceService;
  private final AlarmService alarmService;
  private final WebSocketService webSocketService;

  @Override
  public void handle(Map<String, IoResponse> response) {
    var histories = alarmPersistenceService.getAllHistoriesByStatus(AlarmStatus.SENT);
    var solvedHistories =
        histories.stream()
            .filter(
                history -> {
                  var condition = history.getCondition();
                  var currentValue = response.get(condition.getSensorConfiguration().getAddress());
                  return condition.isMet(currentValue.getPlcValue().getDouble());
                })
            .toList();
    alarmService.updateStatusHistories(solvedHistories, AlarmStatus.SOLVED);

    // fire an empty event when has solved alarms
    if (!histories.isEmpty()) {
      webSocketService.fireAlarm(Map.of());
    }
  }
}
