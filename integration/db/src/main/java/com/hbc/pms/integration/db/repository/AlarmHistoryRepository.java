package com.hbc.pms.integration.db.repository;

import com.hbc.pms.core.model.enums.AlarmStatus;
import com.hbc.pms.integration.db.entity.AlarmHistoryEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AlarmHistoryRepository extends CrudRepository<AlarmHistoryEntity, Long> {

  @Query(
      value =
          "SELECT * "
              + "FROM alarm_history "
              + "WHERE status <> 'SOLVED' AND condition_id = :condition_id",
      nativeQuery = true)
  Optional<AlarmHistoryEntity> findUnsolvedByConditionId(@Param("condition_id") Long id);

  @Query(
      value =
          "SELECT ah FROM AlarmHistoryEntity ah"
              + " WHERE (:status is null or ah.status = :status)")
  List<AlarmHistoryEntity> findAllByStatus(AlarmStatus status);
}
