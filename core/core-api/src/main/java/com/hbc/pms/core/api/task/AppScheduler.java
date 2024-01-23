package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.service.AlarmService;
import com.hbc.pms.core.api.service.BlueprintService;
import com.hbc.pms.core.api.service.WebSocketService;
import com.hbc.pms.core.api.support.data.DataFetcher;
import com.hbc.pms.core.api.support.data.DataProcessor;
import com.hbc.pms.core.api.support.data.WebSocketPublisher;
import com.hbc.pms.core.api.util.CronUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppScheduler {
    private final BlueprintService blueprintService;
    private final DataFetcher dataFetcher;
    private final DataProcessor dataProcessor;
    private final WebSocketPublisher webSocketPublisher;
    private final WebSocketService webSocketService;
    private final AlarmService alarmService;
    private final ConcurrentHashMap<Long, OffsetDateTime> map = new ConcurrentHashMap<>();

    @Scheduled(fixedDelay = 1000)
    public void refreshAllStationsState() {
//        List<Blueprint> blueprintsToFetch = blueprintService
//                .getAll().stream()
//                .filter(blueprint -> webSocketService.countSubscriberOfTopic(blueprint.getName()) > 0)
//                .toList();
//        long startTime = System.currentTimeMillis();
//        Map<String, IoResponse> responseMap = dataFetcher.fetchData(blueprintsToFetch.stream().flatMap(blueprint -> blueprint.getAddresses().stream()).toList());
//        long endTime = System.currentTimeMillis();
//        var processedData = dataProcessor.process(responseMap, blueprintsToFetch);
//        for (Blueprint blueprint : blueprintsToFetch) {
//            webSocketPublisher.fireSendStationData(processedData.get(blueprint.getName()), blueprint.getName());
//            log.info("Processed data: {}", processedData);
//        }
//
//        long duration = (endTime - startTime);
//        log.info("Execution time: " + duration + " milliseconds ");
//        log.info("============================");
    }

    @Scheduled(cron = "*/1 * * * * *")
    public void scheduleAlarmConditions() throws InterruptedException {
        var currentTime = OffsetDateTime.now();
        var conditions = alarmService.getAllConditions();

        var matchedConditions = conditions
            .stream()
              .filter(c -> CronUtil.matchTime(c.getCron(), currentTime) || map.containsKey(c.getId()))
            .toList();
        var matchedAddresses = matchedConditions
            .stream().map(c -> c.getSensorConfiguration().getAddress())
            .toList();

        var responseMap = dataFetcher.fetchData(matchedAddresses);
        matchedConditions
            .forEach(c -> {
                var address = c.getSensorConfiguration().getAddress();
                var currentValue = responseMap.get(address).getPlcValue().getDouble();
                if (c.isMet(currentValue)) {
                    map.remove(c.getId());
                    return;
                }

                if (!map.containsKey(c.getId())) {
                    map.put(c.getId(), OffsetDateTime.now());
                    return;
                }

                var previousTime = map.get(c.getId());
                var duration = Duration.between(previousTime, currentTime);
                if (duration.getSeconds() >= c.getTimeDelay()) {
                    log.info("============ >= 5 ============== \n");
                    map.remove(c.getId());
                }
            });
    }
}
