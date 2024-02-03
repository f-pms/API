package com.hbc.pms.plc.api.scraper;

import com.hbc.pms.plc.api.PlcConfiguration;

import java.util.List;

public interface ScrapeConfiguration {
  PlcConfiguration getPlcConfiguration();

  List<CronScrapeJob> getJobs();
}
