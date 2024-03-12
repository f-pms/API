package com.hbc.pms.core.api

import com.hbc.pms.core.api.service.AlarmConditionPersistenceService
import com.hbc.pms.core.api.service.BlueprintPersistenceService
import com.hbc.pms.core.api.service.SensorConfigurationPersistenceService
import com.hbc.pms.core.api.utils.StringUtils
import com.hbc.pms.core.model.enums.AlarmSeverity
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.integration.db.entity.AlarmConditionEntity
import com.hbc.pms.integration.db.entity.BlueprintEntity
import com.hbc.pms.integration.db.entity.SensorConfigurationEntity
import com.hbc.pms.integration.db.repository.AlarmConditionRepository
import com.hbc.pms.integration.db.repository.BlueprintRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import com.hbc.pms.plc.api.PlcConnector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.concurrent.ThreadLocalRandom
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils

@Component
class TestDataFixture {
  static String PLC_ADDRESS_REAL_01 = "%DB9:13548:REAL"
  static String PLC_ADDRESS_REAL_02 = "%DB9:13552:REAL"

  @Autowired
  AlarmConditionPersistenceService alarmConditionPersistenceService

  @Autowired
  BlueprintRepository blueprintRepository

  @Autowired
  AlarmConditionRepository alarmConditionRepository

  @Autowired
  SensorConfigurationRepository sensorConfigurationRepository

  @Autowired
  BlueprintPersistenceService blueprintPersistenceService

  @Autowired
  SensorConfigurationPersistenceService configurationPersistenceService

  @Autowired
  PlcConnector connector

  void populate() {
    var createdBlueprint = blueprintRepository.save(createBlueprint())
    var sensor = sensorConfigurationRepository.save(createSensorConfiguration(createdBlueprint.id, PLC_ADDRESS_REAL_01))
    sensorConfigurationRepository.save(createSensorConfiguration(createdBlueprint.id, PLC_ADDRESS_REAL_02))
    alarmConditionRepository.save(createCondition(sensor))
    connector.updateScheduler()
  }

  void cleanup() {
    alarmConditionRepository.deleteAll()
    sensorConfigurationRepository.deleteAll()
  }

  static SensorConfigurationEntity createSensorConfiguration(Long blueprintId, String address) {
    return SensorConfigurationEntity.builder()
            .address(address)
            .blueprint(BlueprintEntity.builder().id(blueprintId).build())
            .build()
  }

  static BlueprintEntity createBlueprint() {
    return BlueprintEntity.builder().name("Test-" + RandomStringUtils.random(10, true, true)/* ThreadLocalRandom.current().nextInt(100)*/)
            .description("desc")
            .build()
  }

  static AlarmConditionEntity createCondition(SensorConfigurationEntity sensorConfiguration) {
    return AlarmConditionEntity.builder()
            .isEnabled(true)
            .max(ThreadLocalRandom.current().nextDouble(50, 100))
            .min(ThreadLocalRandom.current().nextDouble(10, 40))
            .cron(StringUtils.buildCronFromSeconds(10))
            .severity(AlarmSeverity.HIGH)
            .timeDelay(ThreadLocalRandom.current().nextInt(5, 10))
            .type(AlarmType.CUSTOM)
            .sensorConfiguration(sensorConfiguration)
            .build()
  }
}
