package com.hbc.pms.core.api.service;

import com.hbc.pms.core.model.ReportRow;
import com.hbc.pms.integration.db.entity.ReportEntity;
import com.hbc.pms.integration.db.entity.ReportRowEntity;
import com.hbc.pms.integration.db.repository.ReportRowRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportRowPersistenceService {
  private final ModelMapper mapper;
  private final ReportRowRepository reportRowRepository;

  public ReportRow create(ReportRow row, Long reportId) {
    var entity = mapper.map(row, ReportRowEntity.class);
    entity.setReport(ReportEntity.builder().id(reportId).build());
    return mapper.map(reportRowRepository.save(entity), ReportRow.class);
  }
}
