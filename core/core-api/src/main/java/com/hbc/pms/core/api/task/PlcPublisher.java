package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.controller.v1.SampleWebSocketController;
import com.hbc.pms.core.api.service.StationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
        var state = stationService.getGeneralState();
        log.info("Station general state:");
        log.info("isConnected: " + state.isConnected());
        log.info("temperature: " + state.temperature());
        log.info("voltage: " + state.voltage());
        sampleWebSocketController.fireSendStationGeneralState(state);
    }

}
