package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.controller.v1.SampleWebSocketController;
import com.hbc.pms.core.api.constants.StationEnum;
import com.hbc.pms.core.api.service.StationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PlcPublisher {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final StationService stationService;
    private final SampleWebSocketController sampleWebSocketController;

    public PlcPublisher(StationService stationService, SampleWebSocketController sampleWebSocketController) {
        this.stationService = stationService;
        this.sampleWebSocketController = sampleWebSocketController;
    }

  @Scheduled(fixedRate = 2000)
  public void refreshStationGeneralState() {
    var tr30State = stationService.getGeneralState(StationEnum.TR30);
    log.info("Station TR30 general state:");
    log.info("isConnected: " + tr30State.isConnected());
    log.info("temperature: " + tr30State.temperature());
    log.info("voltage: " + tr30State.voltage());
    sampleWebSocketController.fireSenTR30StationState(tr30State);

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
  }

}
