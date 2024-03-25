package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.event.RmsHandler;
import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.api.scraper.ResultHandler;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PlcResultHandler implements ResultHandler {

  private final List<RmsHandler> rmsHandlers;

  @Override
  public void handle(
      String job, String alias, OffsetDateTime startTime, Map<String, IoResponse> results) {
    log.debug("Getting response for job={}, alias={}, start-time={}, size={}", job, alias, startTime, results.size());
    for (var handler : rmsHandlers) {
      try {
        handler.handle(startTime, results);
      } catch (Exception exception) {
        log.error("RmsHandler error during processing, skipping", exception);
      }
    }
  }
}
