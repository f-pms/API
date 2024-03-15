package com.hbc.pms.integration.db.specifications;

import com.hbc.pms.core.model.criteria.ReportCriteria;
import com.hbc.pms.integration.db.entity.ReportEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;


import static java.util.Objects.nonNull;

@AllArgsConstructor
public class ReportSpecification implements Specification<ReportEntity> {
  private ReportCriteria criteria;

  @Override
  public Predicate toPredicate(
      Root<ReportEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    var predicates = new ArrayList<>();
    predicates.add(
        builder.between(root.get("recordingDate"), criteria.getStartDate(), criteria.getEndDate()));
    if (nonNull(criteria.getReportTypeId())) {
      var typeRoot = root.join("type");
      predicates.add(builder.equal(typeRoot.get("id"), criteria.getReportTypeId()));
    }
    return builder.and(predicates.toArray(Predicate[]::new));
  }
}
