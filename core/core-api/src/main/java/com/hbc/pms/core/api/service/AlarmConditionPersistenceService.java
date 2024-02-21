package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.support.error.CoreApiException;
import com.hbc.pms.core.api.support.error.ErrorType;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.integration.db.entity.AlarmConditionEntity;
import com.hbc.pms.integration.db.repository.AlarmConditionRepository;
import java.util.List;
import java.util.stream.StreamSupport;
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

    for (var action : entity.getActions()) {
      action.setCondition(entity);
    }

    return mapper.map(alarmConditionRepository.save(entity), AlarmCondition.class);
  }

  public AlarmCondition update(AlarmCondition alarmCondition) {
    AlarmCondition existedCondition = getById(alarmCondition.getId());
    mapper.map(alarmCondition, existedCondition);

    AlarmConditionEntity entity = mapper.map(alarmCondition, AlarmConditionEntity.class);

    return mapper.map(alarmConditionRepository.save(entity), AlarmCondition.class);
  }

  public List<AlarmCondition> getAll() {
    return StreamSupport.stream(alarmConditionRepository.findAll().spliterator(), false)
        .map(b -> mapper.map(b, AlarmCondition.class))
        .toList();
  }

  public AlarmCondition getById(long id) {
    var oCondition = alarmConditionRepository.findById(id);
    if (oCondition.isEmpty()) {
      throw new CoreApiException(
          ErrorType.NOT_FOUND_ERROR, "Not found Alarm Condition with id: " + id);
    }

    return mapper.map(oCondition.get(), AlarmCondition.class);
  }
}
