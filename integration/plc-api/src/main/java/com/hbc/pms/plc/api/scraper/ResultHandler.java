package com.hbc.pms.plc.api.scraper;

import com.hbc.pms.plc.api.IoResponse;
import java.time.OffsetDateTime;
import java.util.Map;

@FunctionalInterface
public interface ResultHandler {

  void handle(HandlerContext context, Map<String, IoResponse> results);
}
