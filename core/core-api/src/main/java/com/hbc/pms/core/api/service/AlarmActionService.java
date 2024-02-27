package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.support.error.CoreApiException;
import com.hbc.pms.core.api.support.error.ErrorType;
import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmActionService {
  private final ModelMapper mapper;
  private final AlarmActionPersistenceService alarmActionPersistenceService;
  private final AlarmConditionPersistenceService alarmConditionPersistenceService;

  public AlarmAction createAlarmAction(Long conditionId, AlarmAction updatingAction) {
    AlarmCondition alarmCondition = alarmConditionPersistenceService.getById(conditionId);

    if (alarmCondition == null) {
      throw new CoreApiException(
          ErrorType.NOT_FOUND_ERROR, "Alarm Condition not found with id: " + conditionId);
    }

    for (AlarmAction action : alarmCondition.getActions()) {
      if (action.getType().equals(updatingAction.getType())) {
        throw new CoreApiException(
            ErrorType.BAD_REQUEST_ERROR,
            "Existed Alarm Action with type: " + updatingAction.getType());
      }
    }

    return alarmActionPersistenceService.create(conditionId, updatingAction);
  }

  public AlarmAction updateAlarmAction(
      Long conditionId, Long actionId, AlarmAction updatingAction) {
    AlarmAction existedAction = alarmActionPersistenceService.getById(actionId);

    if (updatingAction.getType() != existedAction.getType()) {
      throw new CoreApiException(
          ErrorType.BAD_REQUEST_ERROR, "Can not change Alarm Action Type");
    }

    mapper.map(updatingAction, existedAction);

    return alarmActionPersistenceService.update(conditionId, existedAction);
  }
}
