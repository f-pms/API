package com.hbc.pms.core.api.event;

import com.hbc.pms.plc.api.IoResponse;
import java.util.Map;

@FunctionalInterface
public interface RmsHandler {
  void handle(Map<String, IoResponse> response);
}
