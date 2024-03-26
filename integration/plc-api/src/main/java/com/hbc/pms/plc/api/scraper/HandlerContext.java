package com.hbc.pms.plc.api.scraper;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HandlerContext {
  private String jobName;
  private String alias;
  private OffsetDateTime startTime;
}
