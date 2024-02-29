package com.hbc.pms.core.api.service;

import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WebSocketService {

  private static final String TOPIC_PREFIX = "/topic/";
  private final SimpMessagingTemplate template;

  public void fireSendStationData(Map<String, String> stationData, String stationName) {
    this.template.convertAndSend(TOPIC_PREFIX + stationName, stationData);
  }

  public void fireAlarm(Map<String, String> alarmData) {
    template.convertAndSend(TOPIC_PREFIX + "alarm", alarmData);
  }
}
