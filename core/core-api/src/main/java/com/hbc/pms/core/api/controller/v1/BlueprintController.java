package com.hbc.pms.core.api.controller.v1;

import com.hbc.pms.core.api.controller.v1.request.BlueprintRequest;
import com.hbc.pms.core.api.controller.v1.request.SensorConfigurationRequest;
import com.hbc.pms.core.api.controller.v1.request.UpdateSensorConfigurationRequest;
import com.hbc.pms.core.api.controller.v1.response.BlueprintResponse;
import com.hbc.pms.core.api.service.BlueprintPersistenceService;
import com.hbc.pms.core.api.service.SensorConfigurationPersistenceService;
import com.hbc.pms.core.api.support.response.ApiResponse;
import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.core.model.SensorConfiguration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("blueprints")
@RequiredArgsConstructor
public class BlueprintController {
  private final ModelMapper mapper;
  private final BlueprintPersistenceService blueprintPersistenceService;
  private final SensorConfigurationPersistenceService sensorConfigurationPersistenceService;

  @GetMapping()
  public ApiResponse<List<BlueprintResponse>> getBlueprints() {
    var response =
        blueprintPersistenceService.getAll().stream()
            .map(b -> mapper.map(b, BlueprintResponse.class))
            .toList();
    return ApiResponse.success(response);
  }

  @PostMapping()
  public ApiResponse<BlueprintResponse> create(@RequestBody BlueprintRequest body) {
    var blueprint = mapper.map(body, Blueprint.class);
    var response =
        mapper.map(blueprintPersistenceService.create(blueprint), BlueprintResponse.class);
    return ApiResponse.success(response);
  }

  @GetMapping("/{blueprintId}")
  public ApiResponse<BlueprintResponse> getById(@PathVariable Long blueprintId) {
    var response =
        mapper.map(blueprintPersistenceService.getById(blueprintId), BlueprintResponse.class);
    return ApiResponse.success(response);
  }

  @PutMapping("/{blueprintId}")
  public ApiResponse<BlueprintResponse> update(
      @PathVariable Long blueprintId, @RequestBody BlueprintRequest body) {
    var blueprint = mapper.map(body, Blueprint.class);
    blueprint.setId(blueprintId);
    var response =
        mapper.map(blueprintPersistenceService.create(blueprint), BlueprintResponse.class);
    return ApiResponse.success(response);
  }

  @PostMapping("/{blueprintId}/sensor-configurations")
  public ApiResponse<Boolean> createSensorConfiguration(
      @PathVariable Long blueprintId, @RequestBody SensorConfigurationRequest body) {
    var sensorConfiguration = mapper.map(body, SensorConfiguration.class);
    var response = sensorConfigurationPersistenceService.create(blueprintId, sensorConfiguration);
    return ApiResponse.success(response);
  }

  @PutMapping("/{blueprintId}/sensor-configurations/{sensorConfigurationId}")
  public ApiResponse<Boolean> updateSensorConfiguration(
      @PathVariable Long blueprintId,
      @PathVariable Long sensorConfigurationId,
      @RequestBody UpdateSensorConfigurationRequest body) {
    body.aggregateData();
    var sensorConfiguration = mapper.map(body, SensorConfiguration.class);
    sensorConfiguration.setId(sensorConfigurationId);
    var response = sensorConfigurationPersistenceService.update(blueprintId, sensorConfiguration);
    return ApiResponse.success(response);
  }
}
