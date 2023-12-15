package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.controller.v1.SampleWebSocketController;
import com.hbc.pms.core.api.service.StationService;
import com.hbc.pms.core.model.enums.StationEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PlcScheduler {
    private final StationService stationService;
    private final SampleWebSocketController sampleWebSocketController;

    public PlcScheduler(StationService stationService, SampleWebSocketController sampleWebSocketController) {
        this.stationService = stationService;
        this.sampleWebSocketController = sampleWebSocketController;
    }

  @Scheduled(fixedRate = 1000)
  public void refreshStationGeneralState() {
    long startTime = System.currentTimeMillis();

    var tr30State = stationService.getGeneralState(StationEnum.TR30);
    log.info("Station TR30 general state:");
    log.info("isConnected: " + tr30State.isConnected());
    log.info("temperature: " + tr30State.temperature());
    log.info("voltage: " + tr30State.voltage());
    sampleWebSocketController.fireSendTR30StationState(tr30State);

    var tr31State = stationService.getGeneralState(StationEnum.TR31);
//    log.info("Station TR31 general state:");
//    log.info("isConnected: " + tr31State.isConnected());
//    log.info("temperature: " + tr31State.temperature());
//    log.info("voltage: " + tr31State.voltage());

    var tr32State = stationService.getGeneralState(StationEnum.TR32);
//    log.info("Station TR32 general state:");
//    log.info("isConnected: " + tr32State.isConnected());
//    log.info("temperature: " + tr32State.temperature());
//    log.info("voltage: " + tr32State.voltage());

    long endTime = System.currentTimeMillis();
    long duration = (endTime - startTime);
    log.info("Execution time: " + duration + " milliseconds");
    log.info("============================");
  }

}
