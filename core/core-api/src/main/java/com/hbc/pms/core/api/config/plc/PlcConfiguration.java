package com.hbc.pms.core.api.config.plc;

import com.hbc.pms.plc.api.scraper.CronScrapeJob;
import com.hbc.pms.plc.api.scraper.HbcScrapeJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlcConfiguration {

  @Value("${hbc.plc.url}")
  private String plcUrl;

  @Bean
  public com.hbc.pms.plc.api.PlcConfiguration getPlcConfiguration() {
    return com.hbc.pms.plc.api.PlcConfiguration.builder().deviceConnection("HBC", plcUrl).build();
  }

  @Bean
  CronScrapeJob mainJob(
      PlcDataSource plcDataSource, com.hbc.pms.plc.api.PlcConfiguration plcConfiguration) {
    return HbcScrapeJob.builder()
        .jobName("main-processor")
        .plcConfiguration(plcConfiguration)
        .hbcScrapeJobDataSource(plcDataSource)
        .alias("HBC")
        .cron("*/1 * * * * *")
        .build();
  }
}
