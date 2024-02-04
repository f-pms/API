package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.event.RmsHandler;
import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.api.scraper.ResultHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class PlcResultHandler implements ResultHandler {
  private final List<RmsHandler> rmsHandlers;

  @Override
  public void handle(String s, String s1, Map<String, IoResponse> map) {
    log.info("Getting response map with size {}", map.size());
    for (var handler : rmsHandlers) {
      try {
        handler.handle(map);
      } catch (Exception exception) {
        log.error("RmsHandler error during processing, skipping", exception);
      }
    }
  }
}
