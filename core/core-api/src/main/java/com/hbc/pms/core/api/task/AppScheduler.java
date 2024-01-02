package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.domain.Blueprint;
import com.hbc.pms.core.api.service.BlueprintManager;
import com.hbc.pms.core.api.support.data.DataFetcher;
import com.hbc.pms.core.api.support.data.DataProcessor;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppScheduler {
    private final BlueprintManager blueprintManager;
    private final DataFetcher dataFetcher;
    private final DataProcessor dataProcessor;

    public AppScheduler(
        BlueprintManager blueprintManager,
        DataFetcher dataFetcher,
        DataProcessor dataProcessor
    ) {
        this.blueprintManager = blueprintManager;
        this.dataFetcher = dataFetcher;
        this.dataProcessor = dataProcessor;
    }

    @Scheduled(fixedRate = 1000)
    public void refreshAllStationsState() {
        try {
            long startTime = System.currentTimeMillis();
            for (Blueprint blueprint : blueprintManager.getBlueprints()) {
                var rawData = dataFetcher.fetchData(blueprint.getAddresses());
                var processedData = dataProcessor.flattenPLCData(rawData);
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
