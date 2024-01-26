package com.hbc.pms.core.api.support.notification;

import com.hbc.pms.core.model.AlarmCondition;

public interface Channel {
  void notify(String method, AlarmCondition condition);
}
