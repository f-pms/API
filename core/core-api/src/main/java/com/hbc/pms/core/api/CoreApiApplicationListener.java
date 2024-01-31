package com.hbc.pms.core.api;

import com.hbc.pms.core.api.service.BlueprintService;
import com.hbc.pms.plc.api.PlcConnector;
import lombok.AllArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CoreApiApplicationListener {
  private final PlcConnector s7Connector;
  private final BlueprintService blueprintService;

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    s7Connector.runScheduler(blueprintService.getAllAddresses());
  }
}
