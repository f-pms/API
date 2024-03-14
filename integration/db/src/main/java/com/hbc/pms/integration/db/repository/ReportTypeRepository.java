package com.hbc.pms.integration.db.repository;

import com.hbc.pms.integration.db.entity.ReportTypeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ReportTypeRepository extends CrudRepository<ReportTypeEntity, Long> {

  @Query("SELECT t FROM ReportTypeEntity t JOIN FETCH t.reports")
  Iterable<ReportTypeEntity> findAllWithReports();
}
