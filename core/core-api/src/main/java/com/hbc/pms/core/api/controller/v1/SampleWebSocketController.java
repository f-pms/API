package com.hbc.pms.core.api.controller.v1;

import com.hbc.pms.core.api.service.dto.StationGeneralStateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SampleWebSocketController {
    @Autowired
    private SimpMessagingTemplate template;

    @SendTo("/topic/sample-station")
    public StationGeneralStateDto sendStationGeneralState(StationGeneralStateDto stateDto) {
        return stateDto;
    }

    public void fireSendStationGeneralState(StationGeneralStateDto stateDto) {
        this.template.convertAndSend("/topic/sample-station", stateDto);
    }
}
