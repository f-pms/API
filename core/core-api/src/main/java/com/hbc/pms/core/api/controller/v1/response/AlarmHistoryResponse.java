package com.hbc.pms.core.api.controller.v1.response;

import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.enums.AlarmStatus;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class AlarmHistoryResponse {
  private Long id;
  private AlarmStatus status;
  private OffsetDateTime triggeredAt;
  private OffsetDateTime sentAt;
  private OffsetDateTime solvedAt;
  private AlarmCondition condition;

  private BlueprintForHistoryResponse blueprint;

  @Data
  public static class BlueprintForHistoryResponse {
    private Long id;
    private String name;
    private String description;
  }
}
