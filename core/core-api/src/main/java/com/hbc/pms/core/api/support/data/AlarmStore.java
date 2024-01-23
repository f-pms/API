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
  private final ConcurrentHashMap<Long, OffsetDateTime> holdenConditionsMap = new ConcurrentHashMap<>();

  public boolean checkHoldenCondition(Long id) {
    return holdenConditionsMap.containsKey(id);
  }
  
  public List<AlarmCondition> process(List<AlarmCondition> conditions, Map<String, IoResponse> rawData) {
    return conditions
        .stream().filter(c -> {
          var address = c.getSensorConfiguration().getAddress();
          var currentValue = rawData.get(address).getPlcValue().getDouble();
          if (c.isMet(currentValue)) {
            holdenConditionsMap.remove(c.getId());
            return false;
          }

          if (!holdenConditionsMap.containsKey(c.getId())) {
            holdenConditionsMap.put(c.getId(), OffsetDateTime.now());
            return false;
          }

          var previousTime = holdenConditionsMap.get(c.getId());
          var duration = Duration.between(previousTime, OffsetDateTime.now());
          if (duration.getSeconds() < c.getTimeDelay()) {
            return false;
          }

          holdenConditionsMap.remove(c.getId());
          return true;
        })
        .toList();
  }
}
