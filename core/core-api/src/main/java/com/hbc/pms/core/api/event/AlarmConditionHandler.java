package com.hbc.pms.core.api.event;

import com.hbc.pms.core.api.service.alarm.AlarmPersistenceService;
import com.hbc.pms.core.api.service.alarm.AlarmService;
import com.hbc.pms.core.api.support.data.AlarmStore;
import com.hbc.pms.core.api.util.CronUtil;
import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.api.scraper.HandlerContext;
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
  public void handle(HandlerContext context, Map<String, IoResponse> response) {
    var conditions = alarmPersistenceService.getAllConditions();
    var matchedConditions =
        conditions.stream()
            .filter(
                c ->
                    CronUtil.matchTime(c.getCron(), context.getStartTime())
                        || alarmStore.checkHoldingCondition(c.getId()))
            .toList();
    var holdingConditions = alarmStore.process(matchedConditions, response);
    if (holdingConditions.isEmpty()) {
      return;
    }
    alarmService.createHistories(holdingConditions);
  }
}
