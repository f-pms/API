package com.hbc.pms.core.api.event;

import com.hbc.pms.core.api.service.AlarmPersistenceService;
import com.hbc.pms.core.api.service.AlarmService;
import com.hbc.pms.core.api.support.data.AlarmStore;
import com.hbc.pms.core.api.util.CronUtil;
import com.hbc.pms.plc.api.IoResponse;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AlarmConditionHandler implements RmsHandler {
  private final AlarmPersistenceService alarmPersistenceService;
  private final AlarmStore alarmStore;
  private final AlarmService alarmService;

  @Override
  public void handle(Map<String, IoResponse> response) {
    var startTime = OffsetDateTime.now();
    var conditions = alarmPersistenceService.getAllConditions();

    var matchedConditions = conditions
            .stream()
            .filter(c -> CronUtil.matchTime(c.getCron(), startTime) || alarmStore.checkHoldingCondition(c.getId()))
            .toList();
    var holdingConditions = alarmStore.process(matchedConditions, response);
    if (holdingConditions.isEmpty()) {
      return;
    }
    alarmService.createHistories(holdingConditions);
  }
}
