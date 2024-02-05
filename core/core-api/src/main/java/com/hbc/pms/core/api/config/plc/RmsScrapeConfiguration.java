package com.hbc.pms.core.api.config.plc;

import com.hbc.pms.plc.api.PlcConfiguration;
import com.hbc.pms.plc.api.scraper.CronScrapeJob;
import com.hbc.pms.plc.api.scraper.ScrapeConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RmsScrapeConfiguration implements ScrapeConfiguration {
  private final List<CronScrapeJob> cronScrapeJobs;
  private final PlcConfiguration plcConfiguration;

  public RmsScrapeConfiguration(List<CronScrapeJob> cronScrapeJobs, PlcConfiguration plcConfiguration) {
    this.cronScrapeJobs = cronScrapeJobs;
    this.plcConfiguration = plcConfiguration;
  }

  @Override
  public PlcConfiguration getPlcConfiguration() {
    return plcConfiguration;
  }

  @Override
  public List<CronScrapeJob> getJobs() {
    return cronScrapeJobs;
  }
}
