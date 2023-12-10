package com.hbc.core.api.task;

import com.hbc.core.api.service.PlcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PlcPublisher {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final PlcService plcService;

  public PlcPublisher(PlcService plcService) {
    this.plcService = plcService;
  }

  @Scheduled(fixedRate = 1000)
  public void refreshStationGeneralState() {
    var state = plcService.getGeneralState();
    log.info("Station general state:");
    log.info("isConnected: " + state.isConnected());
    log.info("temperature: " + state.temperature());
    log.info("voltage: " + state.voltage());
  }
}
