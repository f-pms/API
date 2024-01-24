package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.service.AlarmService;
import com.hbc.pms.core.api.service.BlueprintService;
import com.hbc.pms.core.api.service.WebSocketService;
import com.hbc.pms.core.api.support.data.AlarmStore;
import com.hbc.pms.core.api.support.data.DataFetcher;
import com.hbc.pms.core.api.support.data.DataProcessor;
import com.hbc.pms.core.api.support.data.WebSocketPublisher;
import com.hbc.pms.core.api.util.CronUtil;
import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.plc.api.IoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppScheduler {
    private static final int ONE_SECOND_DELAY_MILLIS = 1000;
    private static final String EVERY_SECOND_CRON = "*/1 * * * * *";

    private final BlueprintService blueprintService;
    private final DataFetcher dataFetcher;
    private final DataProcessor dataProcessor;
    private final WebSocketPublisher webSocketPublisher;
    private final WebSocketService webSocketService;
    private final AlarmService alarmService;
    private final AlarmStore alarmStore;

    @Scheduled(fixedDelay = ONE_SECOND_DELAY_MILLIS)
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

//        long duration = (endTime - startTime);
//        log.info("Execution time: " + duration + " milliseconds ");
//        log.info("============================");
    }

    @Scheduled(cron = EVERY_SECOND_CRON)
    public void scheduleAlarmConditions() throws InterruptedException {
        var currentTime = OffsetDateTime.now();
        var conditions = alarmService.getAllConditions();

        var matchedConditions = conditions
            .stream()
              .filter(c -> CronUtil.matchTime(c.getCron(), currentTime) || alarmStore.checkHoldingCondition(c.getId()))
            .toList();
        var matchedAddresses = matchedConditions
            .stream().map(c -> c.getSensorConfiguration().getAddress())
            .toList();

        var responseMap = dataFetcher.fetchData(matchedAddresses);
        var holdingConditions = alarmStore.process(matchedConditions, responseMap);
        log.info(holdingConditions.toString()); // TODO: will save to db
    }
}
