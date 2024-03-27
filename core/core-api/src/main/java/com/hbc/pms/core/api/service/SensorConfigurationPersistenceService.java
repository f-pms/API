package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.controller.v1.request.SearchBlueprintCommand;
import com.hbc.pms.core.api.controller.v1.response.SensorConfigurationResponse;
import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.core.model.SensorConfiguration;
import com.hbc.pms.integration.db.entity.BlueprintEntity;
import com.hbc.pms.integration.db.entity.SensorConfigurationEntity;
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository;
import com.hbc.pms.plc.api.PlcConnector;
import com.hbc.pms.support.web.error.CoreApiException;
import com.hbc.pms.support.web.error.ErrorType;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.ValidationException;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SensorConfigurationPersistenceService {
  private final PlcConnector connector;

  private static final String SENSOR_CONFIG_NOT_FOUND_LITERAL =
      "Sensor configuration not found with id: ";
  private final ModelMapper mapper;
  private final SensorConfigurationRepository sensorConfigurationRepository;
  private final AlarmConditionPersistenceService alarmConditionPersistenceService;
  private final PlcService plcService;

  public List<SensorConfiguration> getAll(SearchBlueprintCommand searchCommand) {
    return StreamSupport.stream(
            sensorConfigurationRepository
                .findAllByBlueprint_TypeAndBlueprint_Name(
                    searchCommand.getBlueprintType(), searchCommand.getBlueprintName())
                .spliterator(),
            false)
        .map(b -> mapper.map(b, SensorConfiguration.class))
        .toList();
  }

  public List<SensorConfigurationResponse> getAllWithAlarmStatus(
      SearchBlueprintCommand searchCommand) {
    List<Long> attachedToAlarmIds =
        alarmConditionPersistenceService.getAll().stream()
            .map(e -> e.getSensorConfiguration().getId())
            .toList();

    var result =
        StreamSupport.stream(
                sensorConfigurationRepository
                    .findAllByBlueprint_TypeAndBlueprint_Name(
                        searchCommand.getBlueprintType(), searchCommand.getBlueprintName())
                    .spliterator(),
                false)
            .map(b -> mapper.map(b, SensorConfigurationResponse.class))
            .toList();

    result.forEach(s -> s.setAttachedToAlarm(attachedToAlarmIds.contains(s.getId())));

    return result;
  }

  public SensorConfiguration get(Long id) {
    var entity = sensorConfigurationRepository.findById(id);
    if (entity.isEmpty()) {
      throw new CoreApiException(ErrorType.NOT_FOUND_ERROR, SENSOR_CONFIG_NOT_FOUND_LITERAL + id);
    }
    return mapper.map(entity.get(), SensorConfiguration.class);
  }

  public Blueprint getAssociatedBlueprint(Long id) {
    var entity = sensorConfigurationRepository.findById(id);
    return mapper.map(entity.get().getBlueprint(), Blueprint.class);
  }

  public boolean create(Long blueprintId, SensorConfiguration sensorConfiguration) {
    if (sensorConfiguration.getAddress() != null) {
      var address = sensorConfiguration.getAddress();
      if (!plcService.isTagExisted(address)) {
        throw new ValidationException(
            Collections.singletonList(
                new ErrorMessage("Address " + address + " does not exist in the PLC")));
      }
    }

    var entity = mapper.map(sensorConfiguration, SensorConfigurationEntity.class);
    entity.setBlueprint(BlueprintEntity.builder().id(blueprintId).build());
    sensorConfigurationRepository.save(entity);
    connector.updateScheduler();
    return true;
  }

  public boolean update(Long blueprintId, SensorConfiguration sensorConfiguration) {
    if (sensorConfiguration.getAddress() != null) {
      var address = sensorConfiguration.getAddress();
      if (!plcService.isTagExisted(address)) {
        throw new ValidationException(
            Collections.singletonList(
                new ErrorMessage("Address " + address + " does not exist in the PLC")));
      }
    }

    var entity = mapper.map(sensorConfiguration, SensorConfigurationEntity.class);
    entity.setBlueprint(BlueprintEntity.builder().id(blueprintId).build());

    var oldConfig = sensorConfigurationRepository.findById(entity.getId());
    if (oldConfig.isEmpty()) {
      throw new CoreApiException(
          ErrorType.NOT_FOUND_ERROR, SENSOR_CONFIG_NOT_FOUND_LITERAL + entity.getId());
    }

    var existedEntity = oldConfig.get();
    existedEntity.setAddress(entity.getAddress());
    sensorConfigurationRepository.save(existedEntity);
    connector.updateScheduler();
    return true;
  }

  public boolean delete(SensorConfiguration sensorConfiguration) {
    var entity = mapper.map(sensorConfiguration, SensorConfigurationEntity.class);
    var oldConfig = sensorConfigurationRepository.findById(entity.getId());
    if (oldConfig.isEmpty()) {
      throw new CoreApiException(
          ErrorType.NOT_FOUND_ERROR, SENSOR_CONFIG_NOT_FOUND_LITERAL + entity.getId());
    }
    sensorConfigurationRepository.delete(oldConfig.get());
    return true;
  }
}
