package com.hbc.pms.core.api.controller.v1;

import com.hbc.pms.core.model.StationGeneralState;
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
    public StationGeneralState sendTR30StationState(StationGeneralState stateDto) {
        return stateDto;
    }

    public void fireSendTR30StationState(StationGeneralState stateDto) {
        this.template.convertAndSend("/topic/tr30", stateDto);
    }
}
