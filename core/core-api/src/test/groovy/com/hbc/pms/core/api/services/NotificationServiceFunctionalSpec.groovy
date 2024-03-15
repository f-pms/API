package com.hbc.pms.core.api.services

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.controller.v1.request.CreateAlarmConditionCommand
import com.hbc.pms.core.api.service.AlarmConditionService
import com.hbc.pms.core.api.service.AlarmPersistenceService;
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.core.model.enums.AlarmActionType
import com.hbc.pms.core.model.enums.AlarmSeverity
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.core.model.enums.BlueprintType
import com.hbc.pms.integration.db.entity.AlarmConditionEntity
import com.hbc.pms.integration.db.entity.SensorConfigurationEntity
import com.hbc.pms.integration.db.repository.AlarmConditionRepository
import com.hbc.pms.integration.db.repository.AlarmHistoryRepository
import com.hbc.pms.integration.db.repository.BlueprintRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import com.hbc.pms.plc.api.PlcConnector
import java.util.concurrent.ThreadLocalRandom
import org.springframework.beans.factory.annotation.Autowired;

class NotificationServiceFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  AlarmPersistenceService alarmPersistenceService

  @Autowired
  AlarmConditionService alarmConditionService

  @Autowired
  BlueprintRepository blueprintRepository

  @Autowired
  AlarmConditionRepository conditionRepository

  @Autowired
  AlarmHistoryRepository historyRepository

  @Autowired
  SensorConfigurationRepository configurationRepository

  @Autowired
  PlcConnector connector

  static final def ONE_SECOND_CRON = "*/1 * * * * *"

  def "Noti service - New alarm history with action Popup - Sent noti"() {
    when:
    populateHistories(TestDataFixture.PLC_ADDRESS_REAL_01)

    then:
    Thread.sleep(10000)

  }

  def "Noti service - No alarm history with action Popup - Not sent"() {

  }

  def "Noti service - New alarm history with action Email - Sent noti"() {

  }

  def "Noti service - No alarm history with action Email - Sent noti"() {}

  def "Noti service - New alarm history with 2 actions Popup and Email - Sent all noti"() {
  }

  void populateHistories(String address) {
    def target = address
    conditionRepository.deleteAll()
    historyRepository.deleteAll()
    def blueprint
            = blueprintRepository
            .findAllByTypeAndName(BlueprintType.ALARM, AlarmType.CUSTOM.toString())
            .first()
    def sensorConfig = SensorConfigurationEntity.builder()
            .address(target)
            .blueprint(blueprint)
            .build()
    configurationRepository.save(sensorConfig)

    //TODO: temporarily workaround, delete this 'connector' expression when updated the sensorConfigPersistenceService impl
    connector.updateScheduler()

    def conditionCommand = new CreateAlarmConditionCommand(sensorConfigurationId: sensorConfig.id,
            message: "High temperature detected",
            severity: AlarmSeverity.HIGH,
            type: AlarmType.CUSTOM,
            checkInterval: 30,
            timeDelay: 60,
            min: 20.0,
            max: 30.0,
            isEnabled: true,
            actions: [createDefaultAlarmActionCommand()])
    def condition = alarmConditionService.createAlarmCondition(conditionCommand)

    alarmPersistenceService.createHistoryByCondition(condition)
  }

  def createDefaultAlarmActionCommand() {
    return new CreateAlarmConditionCommand.AlarmActionCommand(type: AlarmActionType.EMAIL,
            message: "Email action's message",
            recipients: new HashSet<String>() {
              {
                add("thisisemail@gmail.com")
                add("haiz@metqua.com")
              }
            })
  }
}
