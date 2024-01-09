package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.service.BlueprintManager;
import com.hbc.pms.core.api.support.data.DataFetcher;
import com.hbc.pms.core.api.support.data.DataProcessor;
import com.hbc.pms.core.api.support.data.WebSocketPublisher;
import com.hbc.pms.core.model.TrackExecutionTime;
import com.hbc.pms.plc.io.Blueprint;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppScheduler {
  private final BlueprintManager blueprintManager;
  private final DataFetcher dataFetcher;
  private final DataProcessor dataProcessor;
  private final WebSocketPublisher webSocketPublisher;

  public AppScheduler(
      BlueprintManager blueprintManager,
      DataFetcher dataFetcher,
      DataProcessor dataProcessor,
      WebSocketPublisher webSocketPublisher) {
    this.blueprintManager = blueprintManager;
    this.dataFetcher = dataFetcher;
    this.dataProcessor = dataProcessor;
    this.webSocketPublisher = webSocketPublisher;
  }

  @Scheduled(fixedRate = 800)
  public void refreshAllStationsState() {
    try {
      for (Blueprint blueprint : blueprintManager.getBlueprints()) {
        log.info(
            "Fetching data for blueprint: {} - {} vars",
            blueprint.getId(),
            blueprint.getAddresses().size());
        var rawData = dataFetcher.fetchData(blueprint.getAddresses());
        var processedData = dataProcessor.flattenToFigureMappedData(rawData, blueprint);
        webSocketPublisher.fireSendStationData(processedData, blueprint.getId());
        log.info("Processed data: {}", processedData);
      }
    } catch (Exception e) {
      log.error("Failed to fetch data from PLC ", e);
    }
  }
}
