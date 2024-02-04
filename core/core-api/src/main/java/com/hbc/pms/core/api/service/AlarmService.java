package com.hbc.pms.core.api.service;

import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.AlarmHistory;
import com.hbc.pms.core.model.enums.AlarmStatus;
import io.vavr.control.Try;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {
  private final AlarmPersistenceService alarmPersistenceService;

  public void createHistories(List<AlarmCondition> conditions) {
    var filteredConditions = conditions.stream()
            .filter(condition -> !alarmPersistenceService.checkExistUnsolvedByConditionId(condition.getId()))
            .toList();
    filteredConditions.forEach(c -> {
      Try.run(() -> alarmPersistenceService.createHistoryByCondition(c))
              .onFailure(throwable -> {
                log.warn("Create history by condition={} failed", c.getId(), throwable);
              });
    });
  }

  public void updateStatusHistories(List<AlarmHistory> histories, AlarmStatus status) {
    histories.forEach(history -> {
      var currentStatus = history.getStatus();
      if (currentStatus.equals(AlarmStatus.SOLVED)) return;
      if (currentStatus.equals(AlarmStatus.SENT)
              && !(status.equals(AlarmStatus.SOLVED))) return;
      if (currentStatus.equals(AlarmStatus.TRIGGERED)
              && !(status.equals(AlarmStatus.SENT) || status.equals(AlarmStatus.SOLVED))) return;
      alarmPersistenceService.updateStatusHistory(history, status);
    });
  }
}
