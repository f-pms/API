package com.hbc.pms.core.model;

import com.hbc.pms.core.model.enums.AlarmStatus;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AlarmHistory {

  private Long id;
  private AlarmStatus status;
  private OffsetDateTime triggeredAt;
  private OffsetDateTime sentAt;
  private OffsetDateTime solvedAt;
  private AlarmCondition condition;
}
