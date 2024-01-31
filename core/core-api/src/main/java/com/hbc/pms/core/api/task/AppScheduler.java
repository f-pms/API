package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.service.BlueprintService;
import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.plc.api.PlcConnector;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppScheduler {
    private final BlueprintService blueprintService;
  private final PlcConnector plcConnector;

  @Scheduled(fixedDelay = 30000)
  public void refreshAllStationsState() {
        List<Blueprint> blueprintsToFetch = blueprintService
                .getAll().stream()
                .toList();
    //    plcConnector.updateScheduler(
    //        blueprintsToFetch.stream()
    //            .flatMap(blueprint -> blueprint.getAddresses().stream())
    //            .toList());
  }
}
