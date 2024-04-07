package com.hbc.pms.integration.db.repository;

import com.hbc.pms.integration.db.entity.ReportEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ReportRepository
    extends CrudRepository<ReportEntity, Long>, JpaSpecificationExecutor<ReportEntity> {
  @Query("SELECT r FROM ReportEntity r JOIN FETCH r.rows WHERE r.id = :id")
  Optional<ReportEntity> findByIdWithRows(@Param("id") Long id);

  @Query("SELECT y FROM ReportEntity y WHERE y.sumJson IS NULL OR y.sumJson = ''")
  @EntityGraph(attributePaths = {"rows", "type"})
  List<ReportEntity> findAllBySumJsonIsNullOrEmpty();
}
