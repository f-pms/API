package com.hbc.core.api.task;

import com.hbc.core.api.domain.Event;
import com.hbc.core.api.domain.Message;
import com.hbc.core.api.service.StationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@Service
public class PlcPublisher implements ApplicationEventPublisherAware {

  private ApplicationEventPublisher publisher;
  private final StationService stationService;

  public PlcPublisher(StationService stationService) {
    this.stationService = stationService;
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

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }
}
