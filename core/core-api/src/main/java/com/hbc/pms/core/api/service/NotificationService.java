package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.support.notification.Channel;
import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.AlarmHistory;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  private final ExecutorService executor = Executors.newFixedThreadPool(5);
  private final List<Channel> channels;

  public void notify(List<AlarmHistory> histories) {
    histories.forEach(
        history -> {
          var condition = history.getAlarmCondition();
          var actions = condition.getActions();
          actions.forEach(action -> notifyAsync(action, condition));
        });
  }

  private void notifyAsync(AlarmAction action, AlarmCondition condition) {
    CompletableFuture.runAsync(
        () -> {
          channels.forEach(c -> c.notify(action, condition));
        },
        executor);
  }
}
