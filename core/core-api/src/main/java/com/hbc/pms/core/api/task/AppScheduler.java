package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.service.BlueprintService;
import com.hbc.pms.core.api.support.data.DataFetcher;
import com.hbc.pms.core.api.support.data.DataProcessor;
import com.hbc.pms.core.api.support.data.WebSocketPublisher;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import com.hbc.pms.plc.io.Blueprint;
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

    @Scheduled(fixedRate = 2000)
    public void refreshAllStationsState() {
        try {
            long startTime = System.currentTimeMillis();
            // TODO: add cache for blueprintService.getAll()
            for (Blueprint blueprint : blueprintService.getAll()) {
                var rawData = dataFetcher.fetchData(blueprint.getAddresses());
                var processedData = dataProcessor.flattenPLCData(rawData);
                webSocketPublisher.fireSendStationData(processedData, blueprint.getId());
                log.info("Processed data: {}", processedData);
            }
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);
            log.info("Execution time: " + duration + " milliseconds");
            log.info("============================");
        } catch (S7Exception e) {
            log.error("Failed to fetch data from PLC");
        }
    }
}
