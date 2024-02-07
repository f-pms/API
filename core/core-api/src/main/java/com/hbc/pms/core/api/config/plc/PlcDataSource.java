package com.hbc.pms.core.api.config.plc;

import com.hbc.pms.core.api.service.BlueprintPersistenceService;
import com.hbc.pms.plc.api.scraper.HbcScrapeJobDataSource;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PlcDataSource implements HbcScrapeJobDataSource {

  private final BlueprintPersistenceService blueprintService;

  @Override
  public Map<String, String> getTags() {
    return blueprintService.getAllMonitoringAddresses().stream()
        .collect(Collectors.toMap(Function.identity(), Function.identity(), (a1, a2) -> a1));
  }
}
