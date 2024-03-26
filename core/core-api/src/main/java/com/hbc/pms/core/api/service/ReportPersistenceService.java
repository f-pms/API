package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.controller.v1.common.Page;
import com.hbc.pms.core.model.Report;
import com.hbc.pms.core.model.criteria.ReportCriteria;
import com.hbc.pms.integration.db.repository.ReportRepository;
import com.hbc.pms.integration.db.specifications.ReportSpecification;
import com.hbc.pms.support.web.error.CoreApiException;
import com.hbc.pms.support.web.error.ErrorType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportPersistenceService {
  private final ModelMapper mapper;
  private final ReportRepository reportRepository;

  public Page<Report> getAll(ReportCriteria criteria, Pageable pagination) {
    var spec = new ReportSpecification(criteria);
    var page = reportRepository.findAll(spec, pagination);
    return Page.<Report>builder()
        .pageTotal(page.getTotalPages())
        .recordTotal(page.getTotalElements())
        .content(page.map(entity -> mapper.map(entity, Report.class)).toList())
        .build();
  }

  public List<Report> getAll(ReportCriteria criteria) {
    var spec = new ReportSpecification(criteria);
    return reportRepository.findAll(spec).stream()
        .map(entity -> mapper.map(entity, Report.class))
        .toList();
  }

  public Report getById(Long id) {
    var oEntity = reportRepository.findByIdWithRows(id);
    if (oEntity.isEmpty()) {
      throw new CoreApiException(ErrorType.NOT_FOUND_ERROR, "Report not found with id: " + id);
    }
    return mapper.map(oEntity, Report.class);
  }
}
