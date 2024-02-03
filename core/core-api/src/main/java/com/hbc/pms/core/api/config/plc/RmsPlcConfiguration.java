package com.hbc.pms.core.api.config.plc;

import com.hbc.pms.plc.api.PlcConfiguration;
import com.hbc.pms.plc.api.scraper.CronScrapeJob;
import com.hbc.pms.plc.api.scraper.HbcScrapeJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RmsPlcConfiguration {
  @Value("${hbc.plc.url}")
  private String plcUrl;

  @Bean
  public PlcConfiguration getPlcConfiguration() {
    return PlcConfiguration.builder()
            .deviceConnection("HBC", plcUrl).build();
  }

  @Bean
  CronScrapeJob mainJob(RmsPlcDataSource rmsPlcDataSource, PlcConfiguration plcConfiguration) {
    return HbcScrapeJob.builder()
            .jobName("main-processor")
            .plcConfiguration(plcConfiguration)
            .hbcScrapeJobDataSource(rmsPlcDataSource)
            .alias("HBC")
            .cron("*/1 * * * * *").build();
  }
}
