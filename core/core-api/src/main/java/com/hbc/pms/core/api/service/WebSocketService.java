package com.hbc.pms.core.api.service;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WebSocketService {
    private final SimpUserRegistry simpUserRegistry;

    public int countSubscriberOfTopic(String topic) {
        return simpUserRegistry.findSubscriptions(r -> r.getDestination().equals("/topic/" + topic)).size();
    }
}
