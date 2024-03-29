package com.hbc.pms.core.api.service.alarm.notification;

import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.AlarmHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

@Slf4j
public abstract class AbstractChannel implements Channel {

  @Retryable(backoff = @Backoff(delay = 1000))
  public void notify(AlarmHistory history, AlarmCondition condition, AlarmAction action) {
    if (!filter(action)) {
      return;
    }

    send(history, condition, action);
  }

  @Recover
  void recover(Exception ex, AlarmHistory history, AlarmCondition condition, AlarmAction action) {
    log.error(
        "Failed to send notification for history={}, action={}, condition={} with exception={}",
        history.getId(),
        action.getId(),
        condition.getId(),
        ex.getMessage());
  }

  protected abstract boolean filter(AlarmAction action);

  protected abstract void send(AlarmHistory history, AlarmCondition condition, AlarmAction action);
}
