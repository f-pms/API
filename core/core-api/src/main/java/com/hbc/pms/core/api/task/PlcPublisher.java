package com.hbc.pms.core.api.task;

import com.hbc.pms.core.api.domain.Event;
import com.hbc.pms.core.api.domain.Message;
import com.hbc.pms.core.api.service.StationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PlcPublisher implements ApplicationEventPublisherAware {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private ApplicationEventPublisher publisher;
  private final StationService stationService;

  public PlcPublisher(StationService stationService) {
    this.stationService = stationService;
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  @Scheduled(fixedRate = 1000)
  public void refreshStationGeneralState() {
    var state = stationService.getGeneralState();
//    log.info("Station general state:");
//    log.info("isConnected: " + state.isConnected());
//    log.info("temperature: " + state.temperature());
//    log.info("voltage: " + state.voltage());
    this.publisher.publishEvent(
        new Event(
            new Message(state.isConnected(), state.temperature(), state.voltage())
        )
    );
  }

}
