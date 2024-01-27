package com.hbc.pms.core.api.service;

import com.hbc.pms.core.model.AlarmCondition;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
