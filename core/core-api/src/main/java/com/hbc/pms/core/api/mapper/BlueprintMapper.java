package com.hbc.pms.core.api.mapper;

import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.integration.db.entity.BlueprintEntity;
import com.hbc.pms.integration.db.entity.SensorConfigurationEntity;
import com.hbc.pms.integration.db.entity.SensorConfigurationFigureEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlueprintMapper {

  BlueprintMapper INSTANCE = Mappers.getMapper(BlueprintMapper.class);

  @Mapping(source = ".", target = ".")
  @Mapping(source = "sensorConfigurations", target = "sensorConfigurations", qualifiedByName = "toSensorConfiguration")
  Blueprint toBlueprint(BlueprintEntity source);

  @Mapping(source = ".", target = ".")
  @Mapping(source = "sensorConfigurationFigures", target = "figures", qualifiedByName = "toFigure")
  @Named("toSensorConfiguration")
  Blueprint.SensorConfiguration toSensorConfiguration(SensorConfigurationEntity source);

  @Mapping(source = ".", target = ".")
  @Mapping(source = "x", target = "displayCoordinate.x")
  @Mapping(source = "y", target = "displayCoordinate.y")
  @Named("toFigure")
  Blueprint.Figure toFigure(SensorConfigurationFigureEntity source);
}
