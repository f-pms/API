package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.event.RmsHandler;
import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.api.scraper.HandlerContext;
import com.hbc.pms.plc.api.scraper.ResultHandler;
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
  public void handle(HandlerContext context, Map<String, IoResponse> results) {
    log.debug(
        "Getting response for job-name={}, alias={}, start-time={}, size={}",
        context.getJobName(),
        context.getAlias(),
        context.getStartTime(),
        results.size());
    for (var handler : rmsHandlers) {
      try {
        handler.handle(context, results);
      } catch (Exception exception) {
        log.error("RmsHandler error during processing, skipping", exception);
      }
    }
  }
}
