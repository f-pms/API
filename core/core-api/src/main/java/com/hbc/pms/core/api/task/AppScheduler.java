package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.service.*;
import com.hbc.pms.core.api.support.data.AlarmStore;
import com.hbc.pms.core.api.support.data.DataFetcher;
import com.hbc.pms.core.api.support.data.DataProcessor;
import com.hbc.pms.core.api.util.CronUtil;
import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.core.model.enums.AlarmStatus;
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
    private static final int HALF_SECOND_DELAY_MILLIS = 500;
    private static final int ONE_SECOND_DELAY_MILLIS = 1000;
    private static final String EVERY_SECOND_CRON = "*/1 * * * * *";

    private final BlueprintPersistenceService blueprintPersistenceService;
    private final DataFetcher dataFetcher;
    private final DataProcessor dataProcessor;
    private final WebSocketService webSocketService;
    private final AlarmPersistenceService alarmPersistenceService;
    private final AlarmService alarmService;
    private final AlarmStore alarmStore;
    private final NotificationService notificationService;


    @Scheduled(cron = EVERY_SECOND_CRON)
    public void scheduleAlarm() {
        var startTime = OffsetDateTime.now();
        var conditions = alarmPersistenceService.getAllConditions();

        var matchedConditions = conditions
            .stream()
              .filter(c -> CronUtil.matchTime(c.getCron(), startTime) || alarmStore.checkHoldingCondition(c.getId()))
            .toList();
        var matchedAddresses = matchedConditions
            .stream().map(c -> c.getSensorConfiguration().getAddress())
            .toList();

        Map<String, IoResponse> responseMap = dataFetcher.fetchData(matchedAddresses);
        var holdingConditions = alarmStore.process(matchedConditions, responseMap);
        if (holdingConditions.isEmpty()) {
            return;
        }
        alarmService.createHistories(holdingConditions);
    }

    @Scheduled(fixedDelay = ONE_SECOND_DELAY_MILLIS)
    public void scheduleNotification() {
        var histories = alarmPersistenceService.getAllHistoriesByStatus(AlarmStatus.TRIGGERED);
        if (histories.isEmpty()) return;
        notificationService.notify(histories);
        alarmService.updateStatusHistories(histories, AlarmStatus.SENT);
    }

    @Scheduled(fixedDelay = ONE_SECOND_DELAY_MILLIS)
    public void scheduleSolveAlarm() {
        var histories = alarmPersistenceService.getAllHistoriesByStatus(AlarmStatus.SENT);
        if (histories.isEmpty()) return;
        var addresses = histories
            .stream()
            .map(history -> history.getAlarmCondition().getSensorConfiguration().getAddress())
            .toList();
        Map<String, IoResponse> responseMap = dataFetcher.fetchData(addresses);
        var solvedHistories = histories
            .stream()
            .filter(history -> {
                var condition = history.getAlarmCondition();
                var currentValue = responseMap.get(condition.getSensorConfiguration().getAddress());
                return condition.isMet(currentValue.getPlcValue().getDouble());
            })
            .toList();
        alarmService.updateStatusHistories(solvedHistories, AlarmStatus.SOLVED);
    }
}
