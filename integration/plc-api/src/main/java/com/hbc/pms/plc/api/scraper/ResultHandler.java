package com.hbc.pms.plc.api.scraper;

import com.hbc.pms.plc.api.IoResponse;

import java.util.Map;

@FunctionalInterface
public interface ResultHandler {
  void handle(String job, String alias, Map<String, IoResponse> results);
}
