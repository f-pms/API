package com.hbc.pms.core.api.service.alarm;

import com.hbc.pms.core.api.constant.ErrorMessageConstant;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.AlarmHistory;
import com.hbc.pms.core.model.enums.AlarmStatus;
import com.hbc.pms.integration.db.entity.AlarmConditionEntity;
import com.hbc.pms.integration.db.entity.AlarmHistoryEntity;
import com.hbc.pms.integration.db.repository.AlarmConditionRepository;
import com.hbc.pms.integration.db.repository.AlarmHistoryRepository;
import com.hbc.pms.support.web.error.CoreApiException;
import com.hbc.pms.support.web.error.ErrorType;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmPersistenceService {

  private final ModelMapper mapper;
  private final AlarmConditionRepository alarmConditionRepository;
  private final AlarmHistoryRepository alarmHistoryRepository;

  public List<AlarmCondition> getAllConditions() {
    return StreamSupport.stream(alarmConditionRepository.findAll().spliterator(), false)
        .map(b -> mapper.map(b, AlarmCondition.class))
        .toList();
  }

  public AlarmCondition getConditionById(Long id) {
    var oCondition = alarmConditionRepository.findById(id);
    if (oCondition.isEmpty()) {
      throw new CoreApiException(
          ErrorType.NOT_FOUND_ERROR, ErrorMessageConstant.ALARM_CONDITION_NOT_FOUND + id);
    }
    return mapper.map(oCondition.get(), AlarmCondition.class);
  }

  public boolean checkExistUnsolvedByConditionId(Long id) {
    return alarmHistoryRepository.findUnsolvedByConditionId(id).isPresent();
  }

  public List<AlarmHistory> getAllHistoriesByStatus(AlarmStatus status) {
    var entities = alarmHistoryRepository.findAllByStatus(status);
    return entities.stream().map(e -> mapper.map(e, AlarmHistory.class)).toList();
  }

  public AlarmHistory createHistoryByCondition(AlarmCondition condition) {
    var conditionEntity = mapper.map(condition, AlarmConditionEntity.class);
    var historyEntity =
        AlarmHistoryEntity.builder()
            .condition(conditionEntity)
            .status(AlarmStatus.TRIGGERED)
            .triggeredAt(OffsetDateTime.now())
            .build();
    return mapper.map(alarmHistoryRepository.save(historyEntity), AlarmHistory.class);
  }

  public void updateHistory(AlarmHistory history) {
    var historyEntity = mapper.map(history, AlarmHistoryEntity.class);
    alarmHistoryRepository.save(historyEntity);
  }
}
