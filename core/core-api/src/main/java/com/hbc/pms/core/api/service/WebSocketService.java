package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.config.WebSocketMetricInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WebSocketService {
    private final WebSocketMetricInterceptor metricInterceptor;

    public int countSubscriberOfTopic(String topic) {
        return metricInterceptor.countSubscriberOfTopic(topic);
    }
}
