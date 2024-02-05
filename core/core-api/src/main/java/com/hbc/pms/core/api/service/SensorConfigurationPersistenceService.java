package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.support.error.CoreApiException;
import com.hbc.pms.core.api.support.error.ErrorType;
import com.hbc.pms.core.model.SensorConfiguration;
import com.hbc.pms.integration.db.entity.BlueprintEntity;
import com.hbc.pms.integration.db.entity.SensorConfigurationEntity;
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SensorConfigurationPersistenceService {

  private final ModelMapper mapper;
  private final SensorConfigurationRepository sensorConfigurationRepository;

  public boolean create(Long blueprintId, SensorConfiguration sensorConfiguration) {
    var entity = mapper.map(sensorConfiguration, SensorConfigurationEntity.class);
    entity.setBlueprint(BlueprintEntity.builder().id(blueprintId).build());
    sensorConfigurationRepository.save(entity);
    return true;
  }

  public boolean update(Long blueprintId, SensorConfiguration sensorConfiguration) {
    var entity = mapper.map(sensorConfiguration, SensorConfigurationEntity.class);
    entity.setBlueprint(BlueprintEntity.builder().id(blueprintId).build());

    var oSensorConfiguration = sensorConfigurationRepository.findById(entity.getId());
    if (oSensorConfiguration.isEmpty()) {
      throw new CoreApiException(
          ErrorType.NOT_FOUND_ERROR, "Sensor configuration not found with id: " + entity.getId());
    }

    var existedEntity = oSensorConfiguration.get();
    existedEntity.setAddress(entity.getAddress());
    sensorConfigurationRepository.save(existedEntity);
    return true;
  }

  public boolean delete(SensorConfiguration sensorConfiguration) {
    var entity = mapper.map(sensorConfiguration, SensorConfigurationEntity.class);
    var oSensorConfiguration = sensorConfigurationRepository.findById(entity.getId());
    if (oSensorConfiguration.isEmpty()) {
      throw new CoreApiException(
          ErrorType.NOT_FOUND_ERROR, "Sensor configuration not found with id: " + entity.getId());
    }
    sensorConfigurationRepository.delete(oSensorConfiguration.get());
    return true;
  }
}
