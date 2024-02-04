package com.hbc.pms.core.api.event;

import com.hbc.pms.core.api.service.AlarmPersistenceService;
import com.hbc.pms.core.api.service.AlarmService;
import com.hbc.pms.core.model.enums.AlarmStatus;
import com.hbc.pms.plc.api.IoResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class AlarmHistoryHandler implements RmsHandler {

  private final AlarmPersistenceService alarmPersistenceService;
  private final AlarmService alarmService;

  @Override
  public void handle(Map<String, IoResponse> response) {
    var histories = alarmPersistenceService.getAllHistoriesByStatus(AlarmStatus.SENT);
    if (histories.isEmpty()) return;
    var solvedHistories = histories
            .stream()
            .filter(history -> {
              var condition = history.getAlarmCondition();
              var currentValue = response.get(condition.getSensorConfiguration().getAddress());
              return condition.isMet(currentValue.getPlcValue().getDouble());
            })
            .toList();
    alarmService.updateStatusHistories(solvedHistories, AlarmStatus.SOLVED);
  }
}
