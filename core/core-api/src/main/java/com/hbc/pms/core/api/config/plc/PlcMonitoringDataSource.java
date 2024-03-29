package com.hbc.pms.core.api.config.plc;

import com.hbc.pms.core.api.service.blueprint.BlueprintPersistenceService;
import com.hbc.pms.core.model.enums.BlueprintType;
import com.hbc.pms.plc.api.scraper.HbcScrapeJobDataSource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PlcMonitoringDataSource implements HbcScrapeJobDataSource {

  private final BlueprintPersistenceService blueprintService;

  @Override
  public Map<String, String> getTags() {
    var monitoringTypes = List.of(BlueprintType.MONITORING, BlueprintType.ALARM);
    return blueprintService.getAll().stream()
        .filter(blueprint -> monitoringTypes.contains(blueprint.getType()))
        .flatMap(blueprint -> blueprint.getAddresses().stream())
        .collect(Collectors.toMap(Function.identity(), Function.identity(), (a1, a2) -> a1));
  }
}
