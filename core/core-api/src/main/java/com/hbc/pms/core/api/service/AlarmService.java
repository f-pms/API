package com.hbc.pms.core.api.service;

import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.integration.db.repository.AlarmConditionRepository;
import com.hbc.pms.integration.db.repository.AlarmHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AlarmService {
  private final ModelMapper mapper;
  private final AlarmConditionRepository alarmConditionRepository;
  private final AlarmHistoryRepository alarmHistoryRepository;

  public List<AlarmCondition> getAllConditions() {
    return StreamSupport
        .stream(alarmConditionRepository.findAll().spliterator(), false)
        .map(b -> mapper.map(b, AlarmCondition.class))
        .toList();
  }

  public AlarmCondition getConditionById(Long id) {
    return mapper.map(alarmConditionRepository.findById(id), AlarmCondition.class);
  }
}
