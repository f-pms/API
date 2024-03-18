package com.hbc.pms.core.api

import com.hbc.pms.core.api.controller.v1.request.CreateAlarmConditionCommand
import com.hbc.pms.core.api.controller.v1.request.UpdateAlarmConditionCommand
import com.hbc.pms.core.api.utils.StringUtils
import com.hbc.pms.core.model.enums.*
import com.hbc.pms.integration.db.entity.*
import com.hbc.pms.integration.db.repository.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.OffsetDateTime
import java.util.concurrent.ThreadLocalRandom

@Component
class TestDataFixture {
  static String PLC_ADDRESS_REAL_01 = "%DB9:13548:REAL"
  static String PLC_ADDRESS_REAL_02 = "%DB9:13552:REAL"
  static String PLC_ADDRESS_REAL_03 = "%DB9:13556:REAL"
  static String PLC_ADDRESS_BOOL_01 = "%DB100:0.0:BOOL"
  static String PLC_ADDRESS_BOOL_02 = "%DB100:1.0:BOOL"

  static Long MONITORING_BLUEPRINT_ID
  static Long CUSTOM_ALARM_BLUEPRINT_ID
  static Long PREDEFINED_ALARM_BLUEPRINT_ID

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

  void populateDefaultBlueprints() {
    def monitoringBlueprint = blueprintRepository.save(createBlueprint(BlueprintType.MONITORING, "Monitoring"))
    MONITORING_BLUEPRINT_ID = monitoringBlueprint.getId()

    def predefinedAlarmBlueprint = blueprintRepository.save(createBlueprint(BlueprintType.ALARM, AlarmType.PREDEFINED.toString()))
    PREDEFINED_ALARM_BLUEPRINT_ID = predefinedAlarmBlueprint.getId()

    def customAlarmBlueprint = blueprintRepository.save(createBlueprint(BlueprintType.ALARM, AlarmType.CUSTOM.toString()))
    CUSTOM_ALARM_BLUEPRINT_ID = customAlarmBlueprint.getId()
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

  static AlarmHistoryEntity createHistory(AlarmConditionEntity alarmCondition, AlarmStatus status) {
    return AlarmHistoryEntity.builder()
            .status(status)
            .triggeredAt(OffsetDateTime.now())
            .condition(alarmCondition)
            .build()
  }

  static AlarmActionEntity createPopupAction(AlarmConditionEntity alarmCondition) {
    return AlarmActionEntity.builder()
            .condition(alarmCondition)
            .type(AlarmActionType.POPUP)
            .message("Popup Action message")
            .build()
  }

  static AlarmActionEntity createPushNotiAction(AlarmConditionEntity alarmCondition) {
    return AlarmActionEntity.builder()
            .condition(alarmCondition)
            .type(AlarmActionType.PUSH_NOTIFICATION)
            .message("Push Notification Action message")
            .build()
  }

  static AlarmActionEntity createEmailAction(AlarmConditionEntity alarmCondition, Set<String> recipients) {
    return AlarmActionEntity.builder()
            .condition(alarmCondition)
            .type(AlarmActionType.EMAIL)
            .message("Email Action message")
            .recipients(recipients)
            .build()
  }

  static AlarmConditionEntity createDefaultConditionEntity(AlarmType alarmType, SensorConfigurationEntity sensorConfig) {
    return AlarmConditionEntity.builder()
            .isEnabled(false)
            .max(ThreadLocalRandom.current().nextDouble(50, 100))
            .min(ThreadLocalRandom.current().nextDouble(10, 40))
            .cron(StringUtils.buildCronFromSeconds(1))
            .severity(AlarmSeverity.HIGH)
            .timeDelay(1)
            .type(alarmType)
            .sensorConfiguration(sensorConfig)
            .build()
  }

  static def createDefaultAlarmActionCommand() {
    return new CreateAlarmConditionCommand.AlarmActionCommand(type: AlarmActionType.EMAIL,
            message: "Email action's message",
            recipients: new HashSet<String>() {
              {
                add("thisisemail@gmail.com")
                add("haiz@metqua.com")
              }
            })
  }

  static def createDefaultAlarmConditionCommand(sensorConfiguration) {
    return new CreateAlarmConditionCommand(sensorConfigurationId: sensorConfiguration.id,
            message: "High temperature detected",
            severity: AlarmSeverity.HIGH,
            type: AlarmType.CUSTOM,
            checkInterval: 30,
            timeDelay: 60,
            min: 20.0,
            max: 30.0,
            isEnabled: true,
            actions: [createDefaultAlarmActionCommand()])
  }

  static def createDefaultUpdateConditionCommand() {
    return new UpdateAlarmConditionCommand(
            severity: AlarmSeverity.HIGH,
            type: AlarmType.CUSTOM,
            checkInterval: ThreadLocalRandom.current().nextInt(1, 3601),
            timeDelay: ThreadLocalRandom.current().nextInt(1, 3601),
            min: ThreadLocalRandom.current().nextDouble(1, 40),
            max: ThreadLocalRandom.current().nextDouble(40, 100),
            isEnabled: true)
  }
}
