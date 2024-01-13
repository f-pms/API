package com.hbc.pms.core.unit.mapper;

import com.hbc.pms.core.api.mapper.BlueprintMapper;
import com.hbc.pms.integration.db.entity.BlueprintEntity;
import com.hbc.pms.integration.db.entity.SensorConfigurationEntity;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BlueprintMapperTest {

  @Test
  void shouldMapEntityToModelSuccessfully() {
    var sensorConfigurationFigureEntity = SensorConfigurationFigureEntity.builder()
        .id(1L)
        .address("DB9.D2060.0")
        .x(1)
        .y(1)
        .build();
    var sensorConfigurationEntity = SensorConfigurationEntity.builder()
        .id(1L)
        .name("sensorConfiguration")
        .sensorConfigurationFigures(Set.of(sensorConfigurationFigureEntity))
        .build();
    var blueprintEntity = BlueprintEntity.builder()
        .id(1L)
        .name("station")
        .description("description")
        .sensorConfigurations(Set.of(sensorConfigurationEntity))
        .build();

    var model = BlueprintMapper.INSTANCE.toBlueprint(blueprintEntity);
    assertAll(
        () -> assertEquals(1L, model.getId()),
        () -> assertEquals("station", model.getName()),
        () -> assertEquals("description", model.getDescription()),
        () -> assertEquals(1L, model.getSensorConfigurations().get(0).getId()),
        () -> assertEquals("sensorConfiguration", model.getSensorConfigurations().get(0).getName()),
        () -> assertEquals(1L, model.getSensorConfigurations().get(0).getSensorConfigurationFigures().get(0).getId()),
        () -> assertEquals("DB9.D2060.0", model.getSensorConfigurations().get(0).getSensorConfigurationFigures().get(0).getAddress()),
        () -> assertEquals(1, model.getSensorConfigurations().get(0).getSensorConfigurationFigures().get(0).getX()),
        () -> assertEquals(1, model.getSensorConfigurations().get(0).getSensorConfigurationFigures().get(0).getY())
    );
  }

  @Test
  void shouldGetAllAddressesSuccessfully() {
    var firstSensorConfigurationFigureEntity = SensorConfigurationFigureEntity.builder()
        .address("DB9.D2060.0")
        .build();
    var secondSensorConfigurationFigureEntity = SensorConfigurationFigureEntity.builder()
        .address("DB10.D2060.0")
        .build();
    var sensorConfigurationEntity = SensorConfigurationEntity.builder()
        .sensorConfigurationFigures(Set.of(firstSensorConfigurationFigureEntity, secondSensorConfigurationFigureEntity))
        .build();
    var blueprintEntity = BlueprintEntity.builder()
        .sensorConfigurations(Set.of(sensorConfigurationEntity))
        .build();

    var model = BlueprintMapper.INSTANCE.toBlueprint(blueprintEntity);
    assertAll(
        () -> assertTrue(model.getAddresses().contains("DB9.D2060.0")),
        () -> assertTrue(model.getAddresses().contains("DB10.D2060.0"))
    );
  }
}
