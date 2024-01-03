package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.service.StationService;
import com.hbc.pms.core.model.enums.StationEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PlcScheduler {

  private final StationService stationService;

  public PlcScheduler(StationService stationService) {
    this.stationService = stationService;
  }

//  @Scheduled(fixedRate = 1000)
  public void refreshStationGeneralState() {
    long startTime = System.currentTimeMillis();

    var tr30State = stationService.getGeneralState(StationEnum.MAIN);
    log.info("Station MAIN general state:");
    log.info("isConnected: " + tr30State.isConnected());
    log.info("temperature: " + tr30State.temperature());
    log.info("voltage: " + tr30State.voltage());

    long endTime = System.currentTimeMillis();
    long duration = (endTime - startTime);
    log.info("Execution time: " + duration + " milliseconds");
    log.info("============================");
  }

}
