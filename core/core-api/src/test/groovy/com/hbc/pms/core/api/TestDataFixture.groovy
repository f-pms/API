package com.hbc.pms.core.api

import com.hbc.pms.core.api.service.AlarmConditionPersistenceService
import com.hbc.pms.core.api.service.BlueprintPersistenceService
import com.hbc.pms.core.api.service.SensorConfigurationPersistenceService
import com.hbc.pms.core.api.utils.StringUtils
import com.hbc.pms.core.model.enums.AlarmSeverity
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.core.model.enums.BlueprintType
import com.hbc.pms.integration.db.entity.AlarmConditionEntity
import com.hbc.pms.integration.db.entity.BlueprintEntity
import com.hbc.pms.integration.db.entity.SensorConfigurationEntity
import com.hbc.pms.integration.db.repository.AlarmActionRepository
import com.hbc.pms.integration.db.repository.AlarmConditionRepository
import com.hbc.pms.integration.db.repository.AlarmHistoryRepository
import com.hbc.pms.integration.db.repository.BlueprintRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import com.hbc.pms.plc.api.PlcConnector
import java.util.concurrent.ThreadLocalRandom
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TestDataFixture {
  static String PLC_ADDRESS_REAL_01 = "%DB9:13548:REAL"
  static String PLC_ADDRESS_REAL_02 = "%DB9:13552:REAL"
  static String PLC_ADDRESS_BOOL_01 = "%DB100:0.0:BOOL"
  static String PLC_ADDRESS_BOOL_02 = "%DB100:1.0:BOOL"

  static Long MONITORING_BLUEPRINT_ID
  static Long CUSTOM_ALARM_BLUEPRINT_ID
  static Long PREDEFINED_ALARM_BLUEPRINT_ID
//  static Long CUSTOM_ALARM_CONDITION_ID
//  static Long PREDEFINED_ALARM_CONDITION_ID
  static Long REAL_SENSOR_WITH_CONDITION_ID
  static Long REAL_SENSOR_WITHOUT_CONDITION_ID
//  static Long BOOL_SENSOR_WITH_CONDITION_ID
//  static Long BOOL_SENSOR_WITHOUT_CONDITION_ID
//  static Long HISTORY_WITH_EMAIL_ACTION_ID
//  static Long HISTORY_WITH_POPUP_ACTION_ID
//  static Long HISTORY_WITH_TWO_ACTIONS_ID

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
  SensorConfigurationRepository configurationRepository

  @Autowired
  BlueprintPersistenceService blueprintPersistenceService

  @Autowired
  SensorConfigurationPersistenceService configurationPersistenceService

  @Autowired
  PlcConnector connector

  void populateDefaultBlueprints() {
    def monitoringBlueprint = blueprintRepository.save(createBlueprint(BlueprintType.MONITORING, "Monitoring"))
    MONITORING_BLUEPRINT_ID = monitoringBlueprint.getId()

    def predefinedAlarmBlueprint = blueprintRepository.save(createBlueprint(BlueprintType.ALARM, AlarmType.PREDEFINED.toString()))
    PREDEFINED_ALARM_BLUEPRINT_ID = predefinedAlarmBlueprint.getId()

    def customAlarmBlueprint = blueprintRepository.save(createBlueprint(BlueprintType.ALARM, AlarmType.CUSTOM.toString()))
    CUSTOM_ALARM_BLUEPRINT_ID = customAlarmBlueprint.getId()

//    def realMonitoringConfig
//            = configurationRepository.save(createSensorConfiguration(monitoringBlueprint, PLC_ADDRESS_REAL_01))
//    REAL_SENSOR_WITH_CONDITION_ID = realMonitoringConfig.getId()
//
//    def realMonitoringConfig2 = configurationRepository.save(createSensorConfiguration(monitoringBlueprint, PLC_ADDRESS_REAL_02))
//    REAL_SENSOR_WITHOUT_CONDITION_ID = realMonitoringConfig2.getId()
//
//    def boolMonitoringConfig
//            = configurationRepository.save(createSensorConfiguration(monitoringBlueprint, PLC_ADDRESS_BOOL_01))
//    BOOL_SENSOR_WITH_CONDITION_ID = boolMonitoringConfig.getId()
//
//    def customAlarmCondition = alarmConditionRepository.save(createDefaultConditionEntity(realMonitoringConfig))
//
//    var sensor = configurationRepository.save(createSensorConfiguration(createdBlueprint.id, PLC_ADDRESS_REAL_01))
//    configurationRepository.save(createSensorConfiguration(createdBlueprint.id, PLC_ADDRESS_REAL_02))
//    var alarmCondition = alarmConditionRepository.save(createDefaultConditionEntity(sensor))
//    alarmActionRepository.save(createAction(alarmCondition))
//    alarmHistoryRepository.save(createHistory(alarmCondition))

    connector.updateScheduler()
  }

  void populateSensorConfigs() {
    def monitoringBlueprint = blueprintRepository.findById(MONITORING_BLUEPRINT_ID).get()

    def realMonitoringConfig
            = configurationRepository.save(createSensorConfiguration(monitoringBlueprint, PLC_ADDRESS_REAL_01))
    REAL_SENSOR_WITH_CONDITION_ID = realMonitoringConfig.getId()
  }

  void cleanup() {
    alarmHistoryRepository.deleteAll()
    alarmActionRepository.deleteAll()
    alarmConditionRepository.deleteAll()
    configurationRepository.deleteAll()
    blueprintRepository.deleteAll()
  }

  static SensorConfigurationEntity createSensorConfiguration(BlueprintEntity blueprint, String address) {
    return SensorConfigurationEntity.builder()
            .address(address)
            .blueprint(blueprint)
            .build()
  }

  static BlueprintEntity createBlueprint(BlueprintType type, String name) {
    return BlueprintEntity.builder()
            .type(type)
            .name(name)
            .description("Description of $type - $name blueprint")
            .build()
  }

//  static AlarmHistoryEntity createHistory(AlarmConditionEntity alarmCondition) {
//    return AlarmHistoryEntity.builder()
//            .status(AlarmStatus.SOLVED)
//            .triggeredAt(OffsetDateTime.now())
//            .condition(alarmCondition)
//            .build()
//  }
//
//  static AlarmActionEntity createAction(AlarmConditionEntity alarmCondition) {
//    return AlarmActionEntity.builder()
//            .condition(alarmCondition)
//            .type(AlarmActionType.POPUP)
//            .message("Popup Action message")
//            .build()
//  }
//
  static AlarmConditionEntity createDefaultConditionEntity(AlarmType alarmType, SensorConfigurationEntity sensorConfig) {
    return AlarmConditionEntity.builder()
            .isEnabled(false)
            .max(ThreadLocalRandom.current().nextDouble(50, 100))
            .min(ThreadLocalRandom.current().nextDouble(10, 40))
            .cron(StringUtils.buildCronFromSeconds(10))
            .severity(AlarmSeverity.HIGH)
            .timeDelay(ThreadLocalRandom.current().nextInt(5, 10))
            .type(alarmType)
            .sensorConfiguration(sensorConfig)
            .build()
  }
}
