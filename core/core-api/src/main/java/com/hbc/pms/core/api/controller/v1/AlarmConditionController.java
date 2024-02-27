package com.hbc.pms.core.api.controller.v1;

import com.hbc.pms.core.api.controller.v1.request.CreateAlarmConditionCommand;
import com.hbc.pms.core.api.controller.v1.request.UpdateAlarmConditionCommand;
import com.hbc.pms.core.api.controller.v1.response.AlarmConditionResponse;
import com.hbc.pms.core.api.service.AlarmActionPersistenceService;
import com.hbc.pms.core.api.service.AlarmActionService;
import com.hbc.pms.core.api.service.AlarmConditionPersistenceService;
import com.hbc.pms.core.api.service.AlarmConditionService;
import com.hbc.pms.core.api.support.response.ApiResponse;
import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("alarm-conditions")
@RequiredArgsConstructor
public class AlarmConditionController {
  private final ModelMapper mapper;
  private final AlarmConditionService alarmConditionService;
  private final AlarmConditionPersistenceService alarmConditionPersistenceService;
  private final AlarmActionService alarmActionService;
  private final AlarmActionPersistenceService alarmActionPersistenceService;

  @PostMapping
  public ApiResponse<AlarmCondition> create(@Valid @RequestBody CreateAlarmConditionCommand body) {
    AlarmCondition result = alarmConditionService.createAlarmCondition(body);
    return ApiResponse.success(result);
  }

  @PostMapping("/{alarmConditionId}/actions")
  public ApiResponse<AlarmAction> createAction(
      @PathVariable Long alarmConditionId,
      @Valid @RequestBody CreateAlarmConditionCommand.AlarmActionCommand body) {
    AlarmAction result =
        alarmActionService.createAlarmAction(alarmConditionId, mapper.map(body, AlarmAction.class));
    return ApiResponse.success(result);
  }

  @PutMapping("/{id}")
  ApiResponse<AlarmCondition> update(
      @PathVariable Long id, @Valid @RequestBody UpdateAlarmConditionCommand body) {
    AlarmCondition result = alarmConditionService.updateAlarmCondition(id, body);

    return ApiResponse.success(result);
  }

  @PutMapping("/{alarmConditionId}/actions/{alarmActionId}")
  public ApiResponse<AlarmAction> updateAction(
      @PathVariable Long alarmConditionId,
      @PathVariable Long alarmActionId,
      @Valid @RequestBody CreateAlarmConditionCommand.AlarmActionCommand body) {
    AlarmAction result =
        alarmActionService.updateAlarmAction(
            alarmConditionId, alarmActionId, mapper.map(body, AlarmAction.class));
    return ApiResponse.success(result);
  }

  @DeleteMapping("/{id}")
  ApiResponse<Boolean> delete(@PathVariable Long id) {
    return ApiResponse.success(alarmConditionPersistenceService.delete(id));
  }

  @DeleteMapping("/{alarmConditionId}/actions/{alarmActionId}")
  public ApiResponse<Boolean> deleteAction(
      @PathVariable Long alarmConditionId, @PathVariable Long alarmActionId) {
    return ApiResponse.success(alarmActionPersistenceService.delete(alarmActionId));
  }

  @GetMapping
  public ApiResponse<List<AlarmConditionResponse>> getAll() {
    List<AlarmConditionResponse> alarmConditions =
        alarmConditionService.getAllWithBlueprints().stream().toList();

    return ApiResponse.success(alarmConditions);
  }

  @GetMapping("/{alarmConditionId}")
  public ApiResponse<AlarmCondition> getById(@PathVariable long alarmConditionId) {
    AlarmCondition condition = alarmConditionPersistenceService.getById(alarmConditionId);

    return ApiResponse.success(condition);
  }
}
