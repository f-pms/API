package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.service.*;
import com.hbc.pms.core.api.support.data.AlarmStore;
import com.hbc.pms.core.api.support.data.DataFetcher;
import com.hbc.pms.core.api.support.data.DataProcessor;
import com.hbc.pms.core.model.enums.AlarmStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

  @Scheduled(fixedDelay = ONE_SECOND_DELAY_MILLIS)
  public void scheduleNotification() {
    var histories = alarmPersistenceService.getAllHistoriesByStatus(AlarmStatus.TRIGGERED);
    if (histories.isEmpty()) return;
    notificationService.notify(histories);
    alarmService.updateStatusHistories(histories, AlarmStatus.SENT);
  }
}
