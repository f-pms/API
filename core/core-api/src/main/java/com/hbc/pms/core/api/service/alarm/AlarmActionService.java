package com.hbc.pms.core.api.service.alarm;

import com.hbc.pms.core.api.constant.ErrorMessageConstant;
import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.support.web.error.CoreApiException;
import com.hbc.pms.support.web.error.ErrorType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
          ErrorType.NOT_FOUND_ERROR, ErrorMessageConstant.ALARM_CONDITION_NOT_FOUND + conditionId);
    }

    for (AlarmAction action : alarmCondition.getActions()) {
      if (action.getType().equals(updatingAction.getType())) {
        throw new CoreApiException(
            ErrorType.BAD_REQUEST_ERROR,
            ErrorMessageConstant.EXISTED_ALARM_ACTION_TYPE + updatingAction.getType());
      }
    }

    return alarmActionPersistenceService.create(conditionId, updatingAction);
  }

  public AlarmAction updateAlarmAction(
      Long conditionId, Long actionId, AlarmAction updatingAction) {
    AlarmAction existedAction = alarmActionPersistenceService.getById(actionId);

    if (updatingAction.getType() != existedAction.getType()) {
      throw new CoreApiException(
          ErrorType.BAD_REQUEST_ERROR, ErrorMessageConstant.CANNOT_CHANGE_ALARM_ACTION_TYPE);
    }

    mapper.map(updatingAction, existedAction);

    return alarmActionPersistenceService.update(conditionId, existedAction);
  }

  @Transactional
  public List<AlarmAction> updateActionMessages(Long conditionId, String message) {
    var existedCondition = alarmConditionPersistenceService.getById(conditionId);
    var actions = existedCondition.getActions();
    actions.forEach(
        action -> {
          action.setMessage(message);
          alarmActionPersistenceService.update(existedCondition.getId(), action);
        });
    return actions;
  }
}
