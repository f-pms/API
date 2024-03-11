package com.hbc.pms.integration.db.repository;

import com.hbc.pms.core.model.enums.BlueprintType;
import com.hbc.pms.integration.db.entity.SensorConfigurationEntity;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SensorConfigurationRepository
    extends CrudRepository<SensorConfigurationEntity, Long> {
  @Query(
      "SELECT sc FROM SensorConfigurationEntity sc"
          + " WHERE (:blueprintType is null or sc.blueprint.type = :blueprintType)"
          + " AND (:blueprintName is null or sc.blueprint.name = :blueprintName)")
  Iterable<SensorConfigurationEntity> findAllByBlueprint_TypeAndBlueprint_Name(
      @Nullable BlueprintType blueprintType, @Nullable String blueprintName);

  List<SensorConfigurationEntity> findAllByAddress(String address);
}
