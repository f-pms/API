package com.hbc.pms.core.api.unit.mapper;

import com.hbc.pms.core.api.mapper.BlueprintMapper;
import com.hbc.pms.integration.db.entity.BlueprintEntity;
import com.hbc.pms.integration.db.entity.SensorConfigurationEntity;
import com.hbc.pms.integration.db.entity.SensorConfigurationFigureEntity;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        () -> assertEquals(1L, model.getSensorConfigurations().get(0).getFigures().get(0).getId()),
        () -> assertEquals("DB9.D2060.0", model.getSensorConfigurations().get(0).getFigures().get(0).getAddress()),
        () -> assertEquals(1, model.getSensorConfigurations().get(0).getFigures().get(0).getDisplayCoordinate().getX()),
        () -> assertEquals(1, model.getSensorConfigurations().get(0).getFigures().get(0).getDisplayCoordinate().getY())
    );
  }
}
