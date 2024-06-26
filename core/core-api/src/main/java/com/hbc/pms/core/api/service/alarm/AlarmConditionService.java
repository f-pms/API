package com.hbc.pms.core.api.service.alarm;

import com.hbc.pms.core.api.constant.ErrorMessageConstant;
import com.hbc.pms.core.api.controller.v1.request.CreateAlarmConditionCommand;
import com.hbc.pms.core.api.controller.v1.request.UpdateAlarmConditionCommand;
import com.hbc.pms.core.api.controller.v1.response.AlarmConditionResponse;
import com.hbc.pms.core.api.service.blueprint.SensorConfigurationPersistenceService;
import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.SensorConfiguration;
import com.hbc.pms.support.web.error.CoreApiException;
import com.hbc.pms.support.web.error.ErrorType;
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
          ErrorType.BAD_REQUEST_ERROR, ErrorMessageConstant.CANNOT_CHANGE_ALARM_CONDITION_TYPE);
    }

    mapper.map(updateCommand, existedCondition);

    // Force set min and max based on UpdateAlarmConditionCommand since they are nullable.
    existedCondition.setMin(updateCommand.getMin());
    existedCondition.setMax(updateCommand.getMax());

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
                    sensorConfigurationPersistenceService.getAssociatedBlueprint(
                        c.getSensorConfiguration().getId()),
                    AlarmConditionResponse.BlueprintForConditionResponse.class)));

    return conditions;
  }
}
