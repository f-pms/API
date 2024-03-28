package com.hbc.pms.core.api.controller.v1;

import com.hbc.pms.core.api.controller.v1.request.SearchBlueprintCommand;
import com.hbc.pms.core.api.controller.v1.response.SensorConfigurationResponse;
import com.hbc.pms.core.api.service.blueprint.SensorConfigurationPersistenceService;
import com.hbc.pms.support.web.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sensor-configurations")
@RequiredArgsConstructor
public class SensorConfigurationController {
  private final SensorConfigurationPersistenceService sensorConfigurationPersistenceService;

  @GetMapping
  public ApiResponse<List<SensorConfigurationResponse>> getAll(
      SearchBlueprintCommand searchCommand) {
    return ApiResponse.success(
        sensorConfigurationPersistenceService.getAllWithAlarmStatus(searchCommand));
  }
}
