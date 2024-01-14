package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.service.BlueprintManager;
import com.hbc.pms.core.api.service.WebSocketService;
import com.hbc.pms.core.api.support.data.DataFetcher;
import com.hbc.pms.core.api.support.data.DataProcessor;
import com.hbc.pms.core.api.support.data.WebSocketPublisher;
import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.io.Blueprint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class AppScheduler {
    private final BlueprintManager blueprintManager;
    private final DataFetcher dataFetcher;
    private final DataProcessor dataProcessor;
    private final WebSocketPublisher webSocketPublisher;
    private final WebSocketService webSocketService;

    public AppScheduler(BlueprintManager blueprintManager, DataFetcher dataFetcher, DataProcessor dataProcessor, WebSocketPublisher webSocketPublisher, WebSocketService webSocketService) {
        this.blueprintManager = blueprintManager;
        this.dataFetcher = dataFetcher;
        this.dataProcessor = dataProcessor;
        this.webSocketPublisher = webSocketPublisher;
        this.webSocketService = webSocketService;
    }

    @Scheduled(fixedDelay = 500)
    public void refreshAllStationsState() {
        List<Blueprint> blueprintsToFetch = blueprintManager
                .getBlueprints().stream()
                .filter(blueprint -> webSocketService.countSubscriberOfTopic(blueprint.getId()) > 0)
                .toList();
        long startTime = System.currentTimeMillis();
        Map<String, IoResponse> responseMap = dataFetcher.fetchData(blueprintsToFetch.stream().flatMap(blueprint -> blueprint.getAddresses().stream()).toList());
        long endTime = System.currentTimeMillis();
        var processedData = dataProcessor.process(responseMap, blueprintsToFetch);
        for (Blueprint blueprint : blueprintsToFetch) {
            webSocketPublisher.fireSendStationData(processedData.get(blueprint.getId()), blueprint.getId());
            log.info("Processed data: {}", processedData);
        }

        long duration = (endTime - startTime);
        log.info("Execution time: " + duration + " milliseconds ");
        log.info("============================");
    }
}
