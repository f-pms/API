package com.hbc.pms.core.api.service.report;

import com.hbc.pms.core.api.service.AbstractPersistenceService;
import com.hbc.pms.core.model.Report;
import com.hbc.pms.core.model.criteria.ReportCriteria;
import com.hbc.pms.integration.db.entity.ReportEntity;
import com.hbc.pms.integration.db.repository.ReportRepository;
import com.hbc.pms.integration.db.specifications.ReportSpecification;
import com.hbc.pms.support.web.error.CoreApiException;
import com.hbc.pms.support.web.error.ErrorType;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportPersistenceService extends AbstractPersistenceService<ReportEntity> {
  private final ModelMapper mapper;
  private final ReportRepository reportRepository;

  public Page<Report> getAll(ReportCriteria criteria, Pageable pagination) {
    var spec = new ReportSpecification(criteria);
    return mapToModel(reportRepository.findAll(spec, pagination), Report.class);
  }

  public Collection<Report> getAll(ReportCriteria criteria) {
    var spec = new ReportSpecification(criteria);
    return mapToModel(reportRepository.findAll(spec), Report.class);
  }

  public Report getById(Long id) {
    var oEntity = reportRepository.findByIdWithRows(id);
    if (oEntity.isEmpty()) {
      throw new CoreApiException(ErrorType.NOT_FOUND_ERROR, "Report not found with id: " + id);
    }
    return mapToModel(oEntity.get(), Report.class);
  }

  public Report create(Report report) {
    var entity = mapper.map(report, ReportEntity.class);
    return mapper.map(reportRepository.save(entity), Report.class);
  }

  public Report update(Long id, Report report) {
    var oEntity = reportRepository.findById(id);
    if (oEntity.isEmpty()) {
      throw new CoreApiException(ErrorType.NOT_FOUND_ERROR, "Report not found with id: " + id);
    }
    var entity = oEntity.get();
    mapper.map(mapper.map(report, ReportEntity.class), entity);
    return mapper.map(reportRepository.save(entity), Report.class);
  }
}
