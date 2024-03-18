package com.hbc.pms.core.api.service;

import com.hbc.pms.core.model.ReportSchedule;
import com.hbc.pms.integration.db.repository.ReportScheduleRepository;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportSchedulePersistenceService {
  private final ModelMapper mapper;
  private final ReportScheduleRepository reportScheduleRepository;

  public List<ReportSchedule> getAll() {
    var entities = reportScheduleRepository.findAll();
    return StreamSupport.stream(entities.spliterator(), false)
        .map(entity -> mapper.map(entity, ReportSchedule.class))
        .toList();
  }
}
