package com.hbc.pms.plc.api.scraper;

import com.hbc.pms.plc.api.PlcConfiguration;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HbcScrapeJob implements CronScrapeJob {

  @NonNull private final String jobName;
  @NonNull private final String alias;
  @NonNull private final HbcScrapeJobDataSource hbcScrapeJobDataSource;
  @NonNull private final String cron;
  @NonNull private final PlcConfiguration plcConfiguration;

  public HbcScrapeJob(
      @NonNull String jobName,
      @NonNull String aliasConnection,
      @NonNull HbcScrapeJobDataSource hbcScrapeJobDataSource,
      @NonNull String cron,
      @NonNull PlcConfiguration plcConfiguration) {
    this.jobName = jobName;
    this.alias = aliasConnection;
    this.hbcScrapeJobDataSource = hbcScrapeJobDataSource;
    this.cron = cron;
    this.plcConfiguration = plcConfiguration;
  }

  public Map<String, String> getTags() {
    return hbcScrapeJobDataSource.getTags();
  }

  public String getSingleConnection() {
    return plcConfiguration.getDeviceConnections().get(alias);
  }
}
