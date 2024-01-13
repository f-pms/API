package com.hbc.pms.integration.db.repository;

import com.hbc.pms.integration.db.entity.SensorConfigurationEntity;
import org.springframework.data.repository.CrudRepository;

public interface SensorConfiguration extends CrudRepository<SensorConfigurationEntity, Long> {

}
