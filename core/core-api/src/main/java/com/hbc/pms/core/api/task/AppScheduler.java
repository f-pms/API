package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.service.alarm.AlarmPersistenceService;
import com.hbc.pms.core.api.service.alarm.AlarmService;
import com.hbc.pms.core.api.service.alarm.NotificationService;
import com.hbc.pms.core.model.enums.AlarmStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppScheduler {

  private static final int ONE_SECOND_DELAY_MILLIS = 1000;
  private final AlarmPersistenceService alarmPersistenceService;
  private final AlarmService alarmService;
  private final NotificationService notificationService;

  @Scheduled(fixedDelay = ONE_SECOND_DELAY_MILLIS)
  public void scheduleNotification() {
    var histories = alarmPersistenceService.getAllHistoriesByStatus(AlarmStatus.TRIGGERED);
    if (histories.isEmpty()) {
      return;
    }
    alarmService.updateStatusHistories(histories, AlarmStatus.SENT);
    notificationService.notify(histories);
  }
}
