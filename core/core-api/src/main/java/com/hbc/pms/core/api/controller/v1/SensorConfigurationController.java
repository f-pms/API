package com.hbc.pms.core.api.controller.v1;

import com.hbc.pms.core.api.controller.v1.request.SearchSensorConfigurationCommand;
import com.hbc.pms.core.api.service.SensorConfigurationPersistenceService;
import com.hbc.pms.core.model.SensorConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("sensor-configurations")
@RequiredArgsConstructor
public class SensorConfigurationController {
  private final SensorConfigurationPersistenceService sensorConfigurationPersistenceService;

  @GetMapping()
  public List<SensorConfiguration> getAll(SearchSensorConfigurationCommand searchCommand) {
    return sensorConfigurationPersistenceService.getAll(searchCommand);
  }
}
