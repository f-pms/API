package com.hbc.pms.core.api.service.alarm;

import com.hbc.pms.core.api.constant.ErrorMessageConstant;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.integration.db.entity.AlarmConditionEntity;
import com.hbc.pms.integration.db.repository.AlarmConditionRepository;
import com.hbc.pms.support.web.error.CoreApiException;
import com.hbc.pms.support.web.error.ErrorType;
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
    if (hasAttachedAlarm(alarmCondition.getSensorConfiguration().getId())) {
      throw new CoreApiException(
          ErrorType.BAD_REQUEST_ERROR,
          ErrorMessageConstant.EXISTED_ALARM_SENSOR_CONFIGURATION
              + alarmCondition.getSensorConfiguration().getId());
    }

    var entity = mapper.map(alarmCondition, AlarmConditionEntity.class);

    for (var action : entity.getActions()) {
      action.setCondition(entity);
    }

    return mapper.map(alarmConditionRepository.save(entity), AlarmCondition.class);
  }

  public AlarmCondition update(AlarmCondition alarmCondition) {
    AlarmConditionEntity entity = mapper.map(alarmCondition, AlarmConditionEntity.class);

    return mapper.map(alarmConditionRepository.save(entity), AlarmCondition.class);
  }

  public boolean delete(Long id) {
    var entity = alarmConditionRepository.findById(id);
    if (entity.isEmpty()) {
      throw new CoreApiException(
          ErrorType.NOT_FOUND_ERROR, ErrorMessageConstant.ALARM_CONDITION_NOT_FOUND + id);
    }

    alarmConditionRepository.delete(entity.get());
    return true;
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
          ErrorType.NOT_FOUND_ERROR, ErrorMessageConstant.ALARM_CONDITION_NOT_FOUND + id);
    }

    return mapper.map(oCondition.get(), AlarmCondition.class);
  }

  public boolean hasAttachedAlarm(Long sensorConfigurationId) {
    return alarmConditionRepository.findBySensorConfiguration_Id(sensorConfigurationId) != null;
  }
}
