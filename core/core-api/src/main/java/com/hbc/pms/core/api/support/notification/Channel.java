package com.hbc.pms.core.api.support.notification;

import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;

@FunctionalInterface
public interface Channel {
  void notify(AlarmAction action, AlarmCondition condition);
}
