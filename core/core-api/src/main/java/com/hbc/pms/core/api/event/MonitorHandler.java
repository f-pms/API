package com.hbc.pms.core.api.event;

import com.hbc.pms.core.api.service.BlueprintPersistenceService;
import com.hbc.pms.core.api.service.WebSocketService;
import com.hbc.pms.core.api.support.data.DataProcessor;
import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.plc.api.IoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MonitorHandler implements RmsHandler {
  private final BlueprintPersistenceService blueprintService;
  private final DataProcessor dataProcessor;
  private final WebSocketService webSocketService;

  @Override
  public void handle(Map<String, IoResponse> response) {
    List<Blueprint> blueprintsToFetch = blueprintService.getAll().stream().toList();
    var processedData = dataProcessor.process(response, blueprintsToFetch);
    for (Blueprint blueprint : blueprintsToFetch) {
      webSocketService.fireSendStationData(
          processedData.get(blueprint.getName()), blueprint.getName());
    }
  }
}
