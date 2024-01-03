package com.hbc.pms.core.api.controller.v1;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class WebSocketController {
    @SendTo("/topic/main")
    public Map<String, String> sendMainStationData(Map<String, String> stationData) {
        return stationData;
    }
}