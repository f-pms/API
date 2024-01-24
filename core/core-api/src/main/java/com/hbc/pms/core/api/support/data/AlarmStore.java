package com.hbc.pms.core.api.support.data;

import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.plc.api.IoResponse;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AlarmStore {
  private final ConcurrentHashMap<Long, OffsetDateTime> holdingConditionsMap = new ConcurrentHashMap<>();

  public boolean checkHoldingCondition(Long id) {
    return holdingConditionsMap.containsKey(id);
  }
  
  public List<AlarmCondition> process(List<AlarmCondition> conditions, Map<String, IoResponse> rawData) {
    return conditions
        .stream().filter(c -> {
          var address = c.getSensorConfiguration().getAddress();
          var currentValue = rawData.get(address).getPlcValue().getDouble();
          if (c.isMet(currentValue)) {
            holdingConditionsMap.remove(c.getId());
            return false;
          }

          if (!holdingConditionsMap.containsKey(c.getId())) {
            holdingConditionsMap.put(c.getId(), OffsetDateTime.now());
            return false;
          }

          var previousTime = holdingConditionsMap.get(c.getId());
          var duration = Duration.between(previousTime, OffsetDateTime.now());
          if (duration.getSeconds() < c.getTimeDelay()) {
            return false;
          }

          holdingConditionsMap.remove(c.getId());
          return true;
        })
        .toList();
  }
}
