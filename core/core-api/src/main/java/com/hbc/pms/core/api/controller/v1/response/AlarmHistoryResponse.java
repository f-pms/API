package com.hbc.pms.core.api.controller.v1.response;

import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.enums.AlarmStatus;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class AlarmHistoryResponse {
  private Long id;
  private AlarmStatus status;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
  private AlarmCondition alarmCondition;
}
