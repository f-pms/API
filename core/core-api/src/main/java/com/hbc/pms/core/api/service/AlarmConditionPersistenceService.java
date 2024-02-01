package com.hbc.pms.core.api.service;

import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.integration.db.entity.AlarmConditionEntity;
import com.hbc.pms.integration.db.entity.BlueprintEntity;
import com.hbc.pms.integration.db.repository.AlarmConditionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmConditionPersistenceService {
  private final ModelMapper mapper;
  private final AlarmConditionRepository alarmConditionRepository;

  public AlarmCondition create(AlarmCondition alarmCondition) {
    var entity = mapper.map(alarmCondition, AlarmConditionEntity.class);
    return mapper.map(alarmConditionRepository.save(entity), AlarmCondition.class);
  }
}
