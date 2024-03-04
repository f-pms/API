package com.hbc.pms.core.api.support.data;

import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.plc.api.IoResponse;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class AlarmStore {

  private final ConcurrentHashMap<Long, OffsetDateTime> holdingConditionsMap =
      new ConcurrentHashMap<>();

  public boolean checkHoldingCondition(Long id) {
    return holdingConditionsMap.containsKey(id);
  }

  public List<AlarmCondition> process(
      List<AlarmCondition> conditions, Map<String, IoResponse> rawData) {
    return conditions.stream()
        .filter(
            condition -> {
              var address = condition.getSensorConfiguration().getAddress();
              if (!rawData.containsKey(address)) {
                return false;
              }

              var currentValue = rawData.get(address).getPlcValue().getDouble();
              if (condition.isMet(currentValue)) {
                holdingConditionsMap.remove(condition.getId());
                return false;
              }

              if (!holdingConditionsMap.containsKey(condition.getId())) {
                holdingConditionsMap.put(condition.getId(), OffsetDateTime.now());
                return false;
              }

              var previousTime = holdingConditionsMap.get(condition.getId());
              var duration = Duration.between(previousTime, OffsetDateTime.now());
              if (duration.getSeconds() < condition.getTimeDelay()) {
                return false;
              }

              holdingConditionsMap.remove(condition.getId());
              return true;
            })
        .toList();
  }
}
