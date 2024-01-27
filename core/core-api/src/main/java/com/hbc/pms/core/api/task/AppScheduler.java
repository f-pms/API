package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.service.BlueprintService;
import com.hbc.pms.core.api.service.WebSocketService;
import com.hbc.pms.core.api.support.data.DataFetcher;
import com.hbc.pms.core.api.support.data.DataProcessor;
import com.hbc.pms.core.api.support.data.WebSocketPublisher;
import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.plc.api.IoResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppScheduler {
    private final BlueprintService blueprintService;
    private final DataFetcher dataFetcher;
    private final DataProcessor dataProcessor;
    private final WebSocketPublisher webSocketPublisher;
    private final WebSocketService webSocketService;

    @Scheduled(fixedDelay = 500)
    public void refreshAllStationsState() {
        List<Blueprint> blueprintsToFetch = blueprintService
                .getAll().stream()
//                .filter(blueprint -> webSocketService.countSubscriberOfTopic(blueprint.getName()) > 0)
                .toList();
        long startTime = System.currentTimeMillis();
        Map<String, IoResponse> responseMap = dataFetcher.fetchData(blueprintsToFetch.stream().flatMap(blueprint -> blueprint.getAddresses().stream()).toList());
        long endTime = System.currentTimeMillis();
        var processedData = dataProcessor.process(responseMap, blueprintsToFetch);
        for (Blueprint blueprint : blueprintsToFetch) {
            webSocketPublisher.fireSendStationData(processedData.get(blueprint.getName()), blueprint.getName());
//            log.info("Processed data: {}", processedData);
        }

        long duration = (endTime - startTime);
        log.info("Execution time: " + duration + " milliseconds ");
        log.info("============================");
    }
}
