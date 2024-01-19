package com.hbc.pms.core.api.support.data;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WebSocketPublisher {
    private final SimpMessagingTemplate template;

    public WebSocketPublisher(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void fireSendStationData(Map<String, String> stationData, String stationName) {
        String topicPrefix = "/topic/";
        this.template.convertAndSend(topicPrefix + stationName, stationData);
    }
}
