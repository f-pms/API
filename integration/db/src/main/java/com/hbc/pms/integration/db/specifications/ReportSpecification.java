package com.hbc.pms.integration.db.specifications;

import com.hbc.pms.core.model.criteria.ReportCriteria;
import com.hbc.pms.core.model.enums.ReportOrder;
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
    var reportTypeRoot = reportRoot.join(ReportEntity_.TYPE);
    var predicates = new ArrayList<Predicate>();
    predicates.add(
        builder.between(
            reportRoot.get(ReportEntity_.RECORDING_DATE),
            criteria.getStartDate(),
            criteria.getEndDate()));

    if (!criteria.getTypeIds().isEmpty()) {
      var in = builder.in(reportTypeRoot.get(ReportTypeEntity_.ID));
      criteria.getTypeIds().forEach(in::value);
      predicates.add(in);
    }

    switch (criteria.getSortBy()) {
      case RECORDING_DATE -> {
        var field = reportRoot.get(ReportEntity_.RECORDING_DATE);
        query.orderBy(
            criteria.getOrder() == ReportOrder.ASC ? builder.asc(field) : builder.desc(field));
      }
      case TYPE -> {
        var field = reportTypeRoot.get(ReportTypeEntity_.NAME);
        query.orderBy(
            criteria.getOrder() == ReportOrder.ASC ? builder.asc(field) : builder.desc(field));
      }
    }

    return builder.and(predicates.toArray(Predicate[]::new));
  }
}
