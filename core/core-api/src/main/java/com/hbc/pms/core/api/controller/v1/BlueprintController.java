package com.hbc.pms.core.api.controller.v1;

import static com.hbc.pms.core.api.config.auth.AuthorizationExpressions.HAS_ROLE_ADMIN;

import com.hbc.pms.core.api.controller.v1.request.BlueprintRequest;
import com.hbc.pms.core.api.controller.v1.request.SearchBlueprintCommand;
import com.hbc.pms.core.api.controller.v1.request.SensorConfigurationRequest;
import com.hbc.pms.core.api.controller.v1.request.UpdateSensorConfigurationCommand;
import com.hbc.pms.core.api.controller.v1.response.BlueprintResponse;
import com.hbc.pms.core.api.service.blueprint.BlueprintPersistenceService;
import com.hbc.pms.core.api.service.blueprint.SensorConfigurationPersistenceService;
import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.core.model.SensorConfiguration;
import com.hbc.pms.support.web.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${apiPrefix}/blueprints")
@RequiredArgsConstructor
public class BlueprintController {

  private final ModelMapper mapper;
  private final BlueprintPersistenceService blueprintPersistenceService;
  private final SensorConfigurationPersistenceService sensorConfigurationPersistenceService;

  @GetMapping
  public ApiResponse<List<BlueprintResponse>> getBlueprints(SearchBlueprintCommand searchCommand) {
    var response =
        blueprintPersistenceService.getAll(searchCommand).stream()
            .map(b -> mapper.map(b, BlueprintResponse.class))
            .toList();
    return ApiResponse.success(response);
  }

  @PostMapping
  @PreAuthorize(HAS_ROLE_ADMIN)
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
  @PreAuthorize(HAS_ROLE_ADMIN)
  public ApiResponse<BlueprintResponse> update(
      @PathVariable Long blueprintId, @RequestBody BlueprintRequest body) {
    var blueprint = mapper.map(body, Blueprint.class);
    blueprint.setId(blueprintId);
    var response =
        mapper.map(blueprintPersistenceService.create(blueprint), BlueprintResponse.class);
    return ApiResponse.success(response);
  }

  @PostMapping("/{blueprintId}/sensor-configurations")
  @PreAuthorize(HAS_ROLE_ADMIN)
  public ApiResponse<Boolean> createSensorConfiguration(
      @PathVariable Long blueprintId, @RequestBody SensorConfigurationRequest body) {
    var sensorConfiguration = mapper.map(body, SensorConfiguration.class);
    var response = sensorConfigurationPersistenceService.create(blueprintId, sensorConfiguration);
    return ApiResponse.success(response);
  }

  @PutMapping("/{blueprintId}/sensor-configurations/{sensorConfigurationId}")
  @PreAuthorize(HAS_ROLE_ADMIN)
  public ApiResponse<Boolean> updateSensorConfiguration(
      @PathVariable Long blueprintId,
      @PathVariable Long sensorConfigurationId,
      @RequestBody UpdateSensorConfigurationCommand body) {
    body.aggregatePlcAddress();
    var sensorConfiguration = mapper.map(body, SensorConfiguration.class);
    sensorConfiguration.setId(sensorConfigurationId);
    var response = sensorConfigurationPersistenceService.update(blueprintId, sensorConfiguration);
    return ApiResponse.success(response);
  }

  @DeleteMapping("/{blueprintId}/sensor-configurations/{sensorConfigurationId}")
  @PreAuthorize(HAS_ROLE_ADMIN)
  public ApiResponse<Boolean> deleteSensorConfiguration(
      @PathVariable Long blueprintId, @PathVariable Long sensorConfigurationId) {
    sensorConfigurationPersistenceService.delete(blueprintId, sensorConfigurationId);
    return ApiResponse.success(true);
  }
}
