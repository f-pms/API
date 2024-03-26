package com.hbc.pms.core.api.event;

import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.api.scraper.HandlerContext;
import java.time.OffsetDateTime;
import java.util.Map;

@FunctionalInterface
public interface RmsHandler {

  void handle(HandlerContext context, Map<String, IoResponse> response);
}
