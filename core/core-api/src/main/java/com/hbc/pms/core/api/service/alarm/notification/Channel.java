package com.hbc.pms.core.api.service.alarm.notification;

import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.AlarmHistory;

@FunctionalInterface
public interface Channel {
  void notify(AlarmHistory history, AlarmCondition condition, AlarmAction action);
}
