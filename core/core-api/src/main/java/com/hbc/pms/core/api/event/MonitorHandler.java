package com.hbc.pms.core.api.event;

import com.hbc.pms.core.api.service.BlueprintService;
import com.hbc.pms.core.api.support.data.DataProcessor;
import com.hbc.pms.core.api.support.data.WebSocketPublisher;
import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.plc.api.IoResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonitorHandler implements RmsHandler {
  private final BlueprintService blueprintService;
  private final DataProcessor dataProcessor;
  private final WebSocketPublisher webSocketPublisher;

  @Override
  public void handle(Map<String, IoResponse> response) {
    List<Blueprint> blueprintsToFetch = blueprintService.getAll().stream().toList();
    var processedData = dataProcessor.process(response, blueprintsToFetch);
    for (Blueprint blueprint : blueprintsToFetch) {
      webSocketPublisher.fireSendStationData(
          processedData.get(blueprint.getName()), blueprint.getName());
    }
  }
}
