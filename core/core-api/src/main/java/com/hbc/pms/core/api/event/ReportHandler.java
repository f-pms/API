package com.hbc.pms.core.api.event;

import com.hbc.pms.core.api.service.ReportPersistenceService;
import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.api.scraper.HandlerContext;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import static com.hbc.pms.core.api.constant.PlcConstant.REPORT_JOB_NAME;

@Service
@RequiredArgsConstructor
public class ReportHandler implements RmsHandler {
  private final ReportPersistenceService reportPersistenceService;

  @Override
  public void handle(HandlerContext context, Map<String, IoResponse> response) {
    if (!context.getJobName().equals(REPORT_JOB_NAME)) {
      return;
    }


  }
}
