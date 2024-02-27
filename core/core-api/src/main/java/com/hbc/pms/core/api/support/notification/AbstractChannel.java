package com.hbc.pms.core.api.support.notification;

import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

@Slf4j
public abstract class AbstractChannel implements Channel {

  @Retryable(backoff = @Backoff(delay = 1000))
  public void notify(AlarmAction action, AlarmCondition condition) {
    if (!filter(action)) {
      return;
    }

    send(action, condition);
  }

  @Recover
  void recover(Exception ex, AlarmAction action, AlarmCondition condition) {
    log.error(
        "Failed to send notification for action={}, condition={} with exception={}",
        action.getId(),
        condition.getId(),
        ex.getMessage());
  }

  protected abstract boolean filter(AlarmAction action);

  protected abstract void send(AlarmAction action, AlarmCondition condition);
}
