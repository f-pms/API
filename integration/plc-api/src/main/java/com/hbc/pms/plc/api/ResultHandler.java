package com.hbc.pms.plc.api;

import java.util.Map;

@FunctionalInterface
public interface ResultHandler {
  void handle(String job, String alias, Map<String, IoResponse> results);
}
