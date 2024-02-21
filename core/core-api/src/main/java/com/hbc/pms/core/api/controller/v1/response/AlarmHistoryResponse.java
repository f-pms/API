package com.hbc.pms.core.api.controller.v1.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.enums.AlarmStatus;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmHistoryResponse {
  private Long id;
  private AlarmStatus status;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private OffsetDateTime createdAt;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private OffsetDateTime updatedAt;
  private AlarmCondition alarmCondition;
}
