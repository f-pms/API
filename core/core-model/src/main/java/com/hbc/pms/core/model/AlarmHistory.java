package com.hbc.pms.core.model;

import com.hbc.pms.core.model.enums.AlarmStatus;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AlarmHistory {
  private Long id;
  private AlarmStatus status;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
  private AlarmCondition alarmCondition;
}
