package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.support.error.CoreApiException;
import com.hbc.pms.core.api.support.error.ErrorType;
import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.integration.db.entity.AlarmActionEntity;
import com.hbc.pms.integration.db.entity.AlarmConditionEntity;
import com.hbc.pms.integration.db.repository.AlarmActionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmActionPersistenceService {
  private final ModelMapper mapper;
  private final AlarmActionRepository alarmActionRepository;

  public AlarmAction getById(Long id) {
    var entity = alarmActionRepository.findById(id);
    if (entity.isEmpty()) {
      throw new CoreApiException(
          ErrorType.NOT_FOUND_ERROR, "Alarm Action not found with id: " + id);
    }

    return mapper.map(entity.get(), AlarmAction.class);
  }

  public AlarmAction create(Long conditionId, AlarmAction alarmAction) {
    var entity = mapper.map(alarmAction, AlarmActionEntity.class);
    entity.setCondition(AlarmConditionEntity.builder().id(conditionId).build());
    return mapper.map(alarmActionRepository.save(entity), AlarmAction.class);
  }

  public AlarmAction update(Long conditionId, AlarmAction alarmAction) {
    AlarmActionEntity entity = mapper.map(alarmAction, AlarmActionEntity.class);
    entity.setCondition(AlarmConditionEntity.builder().id(conditionId).build());

    return mapper.map(alarmActionRepository.save(entity), AlarmAction.class);
  }

  public boolean delete(Long id) {
    var entity = alarmActionRepository.findById(id);
    if (entity.isEmpty()) {
      throw new CoreApiException(
          ErrorType.NOT_FOUND_ERROR, "Alarm Action not found with id: " + id);
    }

    entity.get().getCondition().getActions().removeIf(a -> a.getId().equals(id));

    alarmActionRepository.delete(entity.get());
    return true;
  }
}
