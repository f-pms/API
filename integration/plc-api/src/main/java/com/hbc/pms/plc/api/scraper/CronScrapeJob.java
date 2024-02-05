package com.hbc.pms.plc.api.scraper;


import org.apache.plc4x.java.scraper.ScrapeJob;

import java.util.Map;

public interface CronScrapeJob extends ScrapeJob {
  String getCron();

    String getAlias();

    String getSingleConnection();

    default long getScrapeRate() {
        return 0;
    }

    default Map<String, String> getSourceConnections() {
        return Map.of(getAlias(), getSingleConnection());
    }
}
