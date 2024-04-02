package com.hbc.pms.core.api.config.plc;

import static com.hbc.pms.plc.api.PlcConnectionConstant.DEVICE_NAME;

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
    return com.hbc.pms.plc.api.PlcConfiguration.builder()
        .deviceConnection(DEVICE_NAME, plcUrl)
        .build();
  }

  @Bean
  CronScrapeJob hbcJob(
      PlcDataSource plcDataSource, com.hbc.pms.plc.api.PlcConfiguration plcConfiguration) {
    return HbcScrapeJob.builder()
        .jobName("hbc-processor")
        .plcConfiguration(plcConfiguration)
        .hbcScrapeJobDataSource(plcDataSource)
        .alias(DEVICE_NAME)
        .cron("*/1 * * * * *")
        .build();
  }
}
