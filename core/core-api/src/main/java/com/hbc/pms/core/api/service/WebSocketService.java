package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.config.WebSocketMetricInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class WebSocketService {
    private static final String TOPIC_PREFIX = "/topic/";
    private final WebSocketMetricInterceptor metricInterceptor;
    private final SimpMessagingTemplate template;

    public int countSubscriberOfTopic(String topic) {
        return metricInterceptor.countSubscriberOfTopic(topic);
    }

    public void fireSendStationData(Map<String, String> stationData, String stationName) {
        this.template.convertAndSend(TOPIC_PREFIX + stationName, stationData);
    }

    public void fireAlarm(String alarm) {
        template.convertAndSend(TOPIC_PREFIX + "alarm", alarm);
    }
}
