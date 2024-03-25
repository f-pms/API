package com.hbc.pms.core.api.event;

import com.hbc.pms.plc.api.IoResponse;
import java.time.OffsetDateTime;
import java.util.Map;

@FunctionalInterface
public interface RmsHandler {

  void handle(OffsetDateTime startTime, Map<String, IoResponse> response);
}
