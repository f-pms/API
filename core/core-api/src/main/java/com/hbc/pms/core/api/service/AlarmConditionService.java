package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.controller.v1.request.CreateAlarmConditionCommand;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.SensorConfiguration;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmConditionService {
  private final ModelMapper modelMapper;
  private final SensorConfigurationPersistenceService sensorConfigurationPersistenceService;
  private final AlarmConditionPersistenceService alarmConditionPersistenceService;

  public AlarmCondition createAlarmCondition(CreateAlarmConditionCommand createCommand) {
    SensorConfiguration sensorConfig =
      sensorConfigurationPersistenceService.get(createCommand.getSensorConfigurationId());

    AlarmCondition alarmCondition = modelMapper.map(createCommand, AlarmCondition.class);
    alarmCondition.setEnabled(true);
    alarmCondition.setSensorConfiguration(sensorConfig);

    return alarmConditionPersistenceService.create(alarmCondition);
  }
}
