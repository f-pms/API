package com.hbc.pms.integration.db.repository;

import com.hbc.pms.core.model.enums.BlueprintType;
import com.hbc.pms.integration.db.entity.BlueprintEntity;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface BlueprintRepository extends CrudRepository<BlueprintEntity, Long> {
  @Query("SELECT b FROM BlueprintEntity b" +
          " WHERE (:blueprintType is null or b.type = :blueprintType)" +
          " AND (:blueprintName is null or b.name = :blueprintName)")
  Iterable<BlueprintEntity> findAllByTypeAndName(
          @Nullable BlueprintType blueprintType, @Nullable String blueprintName);
}
