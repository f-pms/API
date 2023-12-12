package com.hbc.pms.core.api.controller.v1;

import com.hbc.pms.core.api.service.dto.StationGeneralStateDto;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SampleWebSocketController {
    private final SimpMessagingTemplate template;

    public SampleWebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @SendTo("/topic/tr30")
    public StationGeneralStateDto sendTR30StationState(StationGeneralStateDto stateDto) {
        return stateDto;
    }

    public void fireSenTR30StationState(StationGeneralStateDto stateDto) {
        this.template.convertAndSend("/topic/tr30", stateDto);
    }
}
