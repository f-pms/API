package com.hbc.pms.core.api.task;

import com.hbc.pms.core.enums.StationEnum;
import com.hbc.pms.core.api.service.StationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PlcPublisher {

  private final StationService stationService;

  public PlcPublisher(StationService stationService) {
    this.stationService = stationService;
  }

  @Scheduled(fixedRate = 1000)
  public void refreshStationGeneralState() {
    var tr30State = stationService.getGeneralState(StationEnum.TR30);
    log.info("Station TR30 general state:");
    log.info("isConnected: " + tr30State.isConnected());
    log.info("temperature: " + tr30State.temperature());
    log.info("voltage: " + tr30State.voltage());

    var tr31State = stationService.getGeneralState(StationEnum.TR31);
    log.info("Station TR31 general state:");
    log.info("isConnected: " + tr31State.isConnected());
    log.info("temperature: " + tr31State.temperature());
    log.info("voltage: " + tr31State.voltage());

    var tr32State = stationService.getGeneralState(StationEnum.TR32);
    log.info("Station TR32 general state:");
    log.info("isConnected: " + tr32State.isConnected());
    log.info("temperature: " + tr32State.temperature());
    log.info("voltage: " + tr32State.voltage());
  }

}
