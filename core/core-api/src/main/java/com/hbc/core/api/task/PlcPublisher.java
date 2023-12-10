package com.hbc.core.api.task;

import com.hbc.core.api.service.StationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PlcPublisher {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final StationService stationService;

  public PlcPublisher(StationService stationService) {
    this.stationService = stationService;
  }

  @Scheduled(fixedRate = 1000)
  public void refreshStationGeneralState() {
    var state = stationService.getGeneralState();
    log.info("Station general state:");
    log.info("isConnected: " + state.isConnected());
    log.info("temperature: " + state.temperature());
    log.info("voltage: " + state.voltage());
  }
}
