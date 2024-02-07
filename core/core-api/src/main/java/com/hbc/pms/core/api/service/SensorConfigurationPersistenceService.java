package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.controller.v1.request.SearchSensorConfigurationCommand;
import com.hbc.pms.core.api.support.error.CoreApiException;
import com.hbc.pms.core.api.support.error.ErrorType;
import com.hbc.pms.core.api.support.response.ApiResponse;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.SensorConfiguration;
import com.hbc.pms.core.model.enums.BlueprintType;
import com.hbc.pms.integration.db.entity.BlueprintEntity;
import com.hbc.pms.integration.db.entity.SensorConfigurationEntity;
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class SensorConfigurationPersistenceService {

  private final ModelMapper mapper;
  private final SensorConfigurationRepository sensorConfigurationRepository;

  public List<SensorConfiguration> getAll(SearchSensorConfigurationCommand searchCommand) {
    return StreamSupport
            .stream(sensorConfigurationRepository.findAllByBlueprint_TypeAndBlueprint_Name(
                    searchCommand.getBlueprintType(),
                    searchCommand.getBlueprintName()
            ).spliterator(), false)
            .map(b -> mapper.map(b, SensorConfiguration.class))
            .toList();
  }

  public SensorConfiguration get(Long id) {
    var entity = sensorConfigurationRepository.findById(id);
    if (entity.isEmpty()) {
      throw new CoreApiException(ErrorType.NOT_FOUND_ERROR, "Sensor configuration not found with id: " + id);
    }
    return mapper.map(entity.get(), SensorConfiguration.class);
  }

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
