package com.hbc.pms.plc.integration.plc4x.scraper;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import org.springframework.scheduling.support.CronExpression;

@Getter
@Setter
@Builder
public class HbcScrapeJob implements CronScrapeJob {
  private final String jobName;
  @Singular private final Map<String, String> sourceConnections;

  @Singular private final Map<String, String> tags;
  private final String triggerConfig;
  private final String cron;

  public HbcScrapeJob(
      String jobName,
      Map<String, String> sourceConnections,
      Map<String, String> tags,
      String triggerConfig,
      String cron) {
    this.jobName = jobName;
    this.sourceConnections = sourceConnections;
    this.tags = tags;
    this.triggerConfig = triggerConfig;
    this.cron = cron;
  }

  @Override
  public long getScrapeRate() {
    return 0;
  }
}
