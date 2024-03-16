package com.hbc.pms.integration.db.specifications;

import static java.util.Objects.nonNull;

import com.hbc.pms.core.model.criteria.ReportCriteria;
import com.hbc.pms.integration.db.entity.ReportEntity;
import com.hbc.pms.integration.db.entity.ReportEntity_;
import com.hbc.pms.integration.db.entity.ReportTypeEntity_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@AllArgsConstructor
public class ReportSpecification implements Specification<ReportEntity> {
  private ReportCriteria criteria;

  @Override
  public Predicate toPredicate(
      Root<ReportEntity> reportRoot, CriteriaQuery<?> query, CriteriaBuilder builder) {
    var predicates = new ArrayList<>();
    predicates.add(
        builder.between(
            reportRoot.get(ReportEntity_.RECORDING_DATE),
            criteria.getStartDate(),
            criteria.getEndDate()));
    if (nonNull(criteria.getReportTypeId())) {
      var reportTypeRoot = reportRoot.join(ReportEntity_.TYPE);
      predicates.add(
          builder.equal(reportTypeRoot.get(ReportTypeEntity_.ID), criteria.getReportTypeId()));
    }
    return builder.and(predicates.toArray(Predicate[]::new));
  }
}
