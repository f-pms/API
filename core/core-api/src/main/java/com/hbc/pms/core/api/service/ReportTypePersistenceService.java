package com.hbc.pms.core.api.service;

import com.hbc.pms.core.model.ReportType;
import com.hbc.pms.integration.db.repository.ReportTypeRepository;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportTypePersistenceService {
  private final ModelMapper mapper;
  private final ReportTypeRepository reportTypeRepository;

  public List<ReportType> getAll() {
    var entities = reportTypeRepository.findAll();
    return StreamSupport.stream(entities.spliterator(), false)
        .map(entity -> mapper.map(entity, ReportType.class))
        .toList();
  }
}
