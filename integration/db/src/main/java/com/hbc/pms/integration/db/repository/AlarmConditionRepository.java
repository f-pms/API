package com.hbc.pms.integration.db.repository;

import com.hbc.pms.core.model.enums.BlueprintType;
import com.hbc.pms.integration.db.entity.AlarmConditionEntity;
import com.hbc.pms.integration.db.entity.SensorConfigurationEntity;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AlarmConditionRepository extends CrudRepository<AlarmConditionEntity, Long> {
  AlarmConditionEntity findBySensorConfiguration_Id(Long id);
}
