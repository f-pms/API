package com.hbc.pms.plc.integration.plc4x.scraper;

import org.apache.plc4x.java.scraper.ScrapeJob;

public interface CronScrapeJob extends ScrapeJob {
  String getCron();
}
