package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.controller.v1.request.CreateAlarmConditionCommand;
import com.hbc.pms.core.api.controller.v1.request.UpdateAlarmConditionCommand;
import com.hbc.pms.core.api.controller.v1.response.AlarmConditionResponse;
import com.hbc.pms.core.api.controller.v1.response.BlueprintResponse;
import com.hbc.pms.core.api.support.error.CoreApiException;
import com.hbc.pms.core.api.support.error.ErrorType;
import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.SensorConfiguration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmConditionService {
  private final ModelMapper mapper;
  private final SensorConfigurationPersistenceService sensorConfigurationPersistenceService;
  private final AlarmConditionPersistenceService alarmConditionPersistenceService;

  public AlarmCondition createAlarmCondition(CreateAlarmConditionCommand createCommand) {
    SensorConfiguration sensorConfig =
        sensorConfigurationPersistenceService.get(createCommand.getSensorConfigurationId());

    AlarmCondition alarmCondition = mapper.map(createCommand, AlarmCondition.class);
    alarmCondition.setEnabled(true);
    alarmCondition.setSensorConfiguration(sensorConfig);
    for (AlarmAction action : alarmCondition.getActions()) {
      action.setMessage(createCommand.getMessage());
    }

    return alarmConditionPersistenceService.create(alarmCondition);
  }

  public AlarmCondition updateAlarmCondition(Long id, UpdateAlarmConditionCommand updateCommand) {
    AlarmCondition existedCondition = alarmConditionPersistenceService.getById(id);

    if (updateCommand.getType() != existedCondition.getType()) {
      throw new CoreApiException(
          ErrorType.BAD_REQUEST_ERROR, "Can not change Alarm Condition Type");
    }

    mapper.map(updateCommand, existedCondition);

    return alarmConditionPersistenceService.update(existedCondition);
  }

  public List<AlarmConditionResponse> getAllWithBlueprints() {
    List<AlarmConditionResponse> conditions =
        alarmConditionPersistenceService.getAll().stream()
            .map(c -> mapper.map(c, AlarmConditionResponse.class))
            .toList();

    conditions.forEach(
        c ->
            c.setBlueprint(
                mapper.map(
                    sensorConfigurationPersistenceService.getAssociatedBlueprint(c.getId()),
                    AlarmConditionResponse.BlueprintForConditionResponse.class)));

    return conditions;
  }
}
