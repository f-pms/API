package com.hbc.pms.core.api

import com.hbc.pms.core.api.service.AlarmConditionPersistenceService
import com.hbc.pms.core.api.service.BlueprintPersistenceService
import com.hbc.pms.core.api.service.SensorConfigurationPersistenceService
import com.hbc.pms.core.api.utils.StringUtils
import com.hbc.pms.core.model.enums.AlarmActionType
import com.hbc.pms.core.model.enums.AlarmSeverity
import com.hbc.pms.core.model.enums.AlarmStatus
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.integration.db.entity.*
import com.hbc.pms.integration.db.repository.*
import com.hbc.pms.plc.api.PlcConnector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils

import java.time.OffsetDateTime
import java.util.concurrent.ThreadLocalRandom

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
  AlarmHistoryRepository alarmHistoryRepository

  @Autowired
  AlarmActionRepository alarmActionRepository

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
    var alarmCondition = alarmConditionRepository.save(createCondition(sensor))
    alarmActionRepository.save(createAction(alarmCondition))
    alarmHistoryRepository.save(createHistory(alarmCondition))
    connector.updateScheduler()
  }

  void cleanup() {
    alarmHistoryRepository.deleteAll()
    alarmActionRepository.deleteAll()
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

  static AlarmHistoryEntity createHistory(AlarmConditionEntity alarmCondition) {
    return AlarmHistoryEntity.builder()
            .status(AlarmStatus.SOLVED)
            .triggeredAt(OffsetDateTime.now())
            .condition(alarmCondition)
            .build()
  }

  static AlarmActionEntity createAction(AlarmConditionEntity alarmCondition) {
    return AlarmActionEntity.builder()
            .condition(alarmCondition)
            .type(AlarmActionType.POPUP)
            .message("Popup Action message")
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
