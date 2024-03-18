package com.hbc.pms.core.api.services

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
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
import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired

@EnableSharedInjection
class AlarmFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  SensorConfigurationRepository configurationRepository

  @Autowired
  AlarmConditionRepository conditionRepository

  @Autowired
  BlueprintRepository blueprintRepository

  @Autowired
  PlcConnector connector

  @Autowired
  AlarmHistoryRepository historyRepository

  static final def ONE_SECOND_CRON = "*/1 * * * * *"
  static final def DELAY_SEC_PREDEFINED_ALARM = 5
  static final def DELAY_SEC_CUSTOM_ALARM = 8

  def "Alarm Websocket - PREDEFINED Alarm with 1s checkInterval and 1s timeDelay not met - Not triggered"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_BOOL_01
    populateTestAlarm(AlarmType.PREDEFINED, target, null, null)
    def historyCountBefore = historyRepository.findAll().size()

    when:
    plcValueTestFactory.setCurrentValue(target, false)

    then:
    Thread.sleep(DELAY_SEC_PREDEFINED_ALARM * 1000)
    def historyCountAfter = historyRepository.findAll().size()
    historyCountBefore == historyCountAfter
  }

  def "Alarm Websocket - PREDEFINED Alarm with 1s checkInterval and 1s timeDelay met - Triggered correctly"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_BOOL_01
    populateTestAlarm(AlarmType.PREDEFINED, target, null, null)
    def historyCountBefore = historyRepository.findAll().size()

    when:
    plcValueTestFactory.setCurrentValue(target, true)

    then:
    conditions.within(DELAY_SEC_PREDEFINED_ALARM, {
      def historyCountAfter = historyRepository.findAll().size()
      historyCountAfter == historyCountBefore + 1
    })
  }

  def "Alarm Websocket - CUSTOM Alarm with min and max range and not met - Not triggered"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_REAL_01
    populateTestAlarm(AlarmType.CUSTOM, target, 10, 20)
    def historyCountBefore = historyRepository.findAll().size()

    when:
    plcValueTestFactory.setCurrentValue(target, value)

    then:
    Thread.sleep(DELAY_SEC_CUSTOM_ALARM * 1000)
    def historyCountAfter = historyRepository.findAll().size()
    historyCountAfter == historyCountBefore

    where:
    value << [10f, 15f, 20f]
  }

  def "Alarm Websocket - CUSTOM Alarm with min and max range and met - Triggered correctly"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_REAL_01
    populateTestAlarm(AlarmType.CUSTOM, target, 10, 20)
    def historyCountBefore = historyRepository.findAll().size()

    when:
    plcValueTestFactory.setCurrentValue(target, value)

    then:
    conditions.within(DELAY_SEC_CUSTOM_ALARM, {
      def historyCountAfter = historyRepository.findAll().size()
      historyCountAfter == historyCountBefore + 1
    })

    where:
    value << [5f, 25f]
  }

//  @PendingFeature
  def "Alarm Websocket - CUSTOM Alarm with min only and not met - Not triggered"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_REAL_01
    populateTestAlarm(AlarmType.CUSTOM, target, 10, null)
    def historyCountBefore = historyRepository.findAll().size()

    when:
    plcValueTestFactory.setCurrentValue(target, value)

    then:
    Thread.sleep(DELAY_SEC_CUSTOM_ALARM * 1000)
    def historyCountAfter = historyRepository.findAll().size()
    historyCountAfter == historyCountBefore

    where:
    value << [10f, 15f, 20f]
  }

  def "Alarm Websocket - CUSTOM Alarm with min only range and met - Triggered correctly"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_REAL_01
    populateTestAlarm(AlarmType.CUSTOM, target, 10, null)
    def historyCountBefore = historyRepository.findAll().size()

    when:
    plcValueTestFactory.setCurrentValue(target, 5f)

    then:
    conditions.within(DELAY_SEC_CUSTOM_ALARM, {
      def historyCountAfter = historyRepository.findAll().size()
      historyCountAfter == historyCountBefore + 1
    })
  }

  def "Alarm Websocket - CUSTOM Alarm with max only and not met - Not triggered"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_REAL_01

    populateTestAlarm(AlarmType.CUSTOM, target, null, 10)
    def historyCountBefore = historyRepository.findAll().size()

    when:
    plcValueTestFactory.setCurrentValue(target, value)

    then:
    Thread.sleep(DELAY_SEC_CUSTOM_ALARM * 1000)
    def historyCountAfter = historyRepository.findAll().size()
    historyCountAfter == historyCountBefore

    where:
    value << [5f, 10f]
  }

  def "Alarm Websocket - CUSTOM Alarm with max only range and met - Triggered correctly"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_REAL_01
    populateTestAlarm(AlarmType.CUSTOM, target, null, 10)
    def historyCountBefore = historyRepository.findAll().size()

    when:
    plcValueTestFactory.setCurrentValue(target, 15f)

    then:
    conditions.within(DELAY_SEC_CUSTOM_ALARM, {
      def historyCountAfter = historyRepository.findAll().size()
      historyCountAfter == historyCountBefore + 1
    })
  }

  void populateTestAlarm(AlarmType alarmType, String address, Double min, Double max) {
    def target = address
    conditionRepository.deleteAll()
    def blueprint
            = blueprintRepository
            .findAllByTypeAndName(BlueprintType.ALARM, alarmType.toString())
            .first()
    def sensorConfig = SensorConfigurationEntity.builder()
            .address(target)
            .blueprint(blueprint)
            .build()
    configurationRepository.save(sensorConfig)

    //TODO: temporarily workaround, change later when updated the sensorConfigPersistenceService impl
    connector.updateScheduler()

    def condition = AlarmConditionEntity.builder()
            .cron(ONE_SECOND_CRON)
            .isEnabled(true)
            .severity(AlarmSeverity.HIGH)
            .type(alarmType)
            .min(min)
            .max(max)
            .sensorConfiguration(sensorConfig)
            .timeDelay(1)
            .build()
    conditionRepository.save(condition)
  }
}
