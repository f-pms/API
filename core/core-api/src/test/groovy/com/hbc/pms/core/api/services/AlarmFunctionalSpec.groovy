package com.hbc.pms.core.api.services

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.core.model.enums.BlueprintType
import com.hbc.pms.integration.db.entity.AlarmConditionEntity
import com.hbc.pms.integration.db.entity.BlueprintEntity
import com.hbc.pms.integration.db.entity.SensorConfigurationEntity
import com.hbc.pms.integration.db.repository.AlarmConditionRepository
import com.hbc.pms.integration.db.repository.AlarmHistoryRepository
import com.hbc.pms.integration.db.repository.BlueprintRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.PendingFeature
import spock.lang.Shared
import spock.util.concurrent.PollingConditions

@EnableSharedInjection
class AlarmFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  SensorConfigurationRepository configurationRepository

  @Autowired
  AlarmConditionRepository conditionRepository

  @Autowired
  @Shared
  BlueprintRepository blueprintRepository

  @Autowired
  AlarmHistoryRepository historyRepository

  static final def ONE_SECOND_CRON = "*/1 * * * * *"
  static final def DELAY_SEC_PREDEFINED_ALARM = 2
  static final def DELAY_SEC_CUSTOM_ALARM = 15

  def setup() {
    conditions = new PollingConditions(timeout: DELAY_SEC_PREDEFINED_ALARM)
  }

  def setupSpec() {
    blueprintRepository.save(createPredefinedAlarmBlueprint())
    blueprintRepository.save(createCustomAlarmBlueprint())
  }

  def "Alarm Websocket - PREDEFINED Alarm with 1s checkInterval and 1s timeDelay not met - Not triggered"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_BOOL_01
    createTestAlarm(AlarmType.PREDEFINED, target, null, null)
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
    createTestAlarm(AlarmType.CUSTOM, target, null, null)
    def historyCountBefore = historyRepository.findAll().size()

    when:
    plcValueTestFactory.setCurrentValue(target, true)

    then:
    conditions.within(DELAY_SEC_CUSTOM_ALARM, {
      def historyCountAfter = historyRepository.findAll().size()
      historyCountAfter == historyCountBefore + 1
    })
  }

  def "Alarm Websocket - CUSTOM Alarm with min and max range and not met - Not triggered"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_REAL_01
    createTestAlarm(AlarmType.CUSTOM, target, 10, 20)
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
    createTestAlarm(AlarmType.CUSTOM, target, 10, 20)
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
    Thread.sleep(DELAY_SEC_CUSTOM_ALARM * 1000)
    def target = TestDataFixture.PLC_ADDRESS_REAL_01
    createTestAlarm(AlarmType.CUSTOM, target, null, null)
    def historyCountBefore
    def historyCountAfter

    when:
    historyCountBefore = historyRepository.findAll().size()
    plcValueTestFactory.setCurrentValue(target, 10f)
    Thread.sleep(DELAY_SEC_CUSTOM_ALARM * 1000)
    historyCountAfter = historyRepository.findAll().size()

    then:
    historyCountAfter == historyCountBefore

    when:
    historyCountBefore = historyRepository.findAll().size()
    plcValueTestFactory.setCurrentValue(target, 15f)
    Thread.sleep(DELAY_SEC_CUSTOM_ALARM * 1000)
    historyCountAfter = historyRepository.findAll().size()

    then:
    historyCountAfter == historyCountBefore
  }

  def "Alarm Websocket - CUSTOM Alarm with min only range and met - Triggered correctly"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_REAL_01
    createTestAlarm(AlarmType.CUSTOM, target, 10, null)
    def historyCountBefore = historyRepository.findAll().size()

    when:
    plcValueTestFactory.setCurrentValue(target, 5f)

    then:
    conditions.within(DELAY_SEC_CUSTOM_ALARM, {
      def historyCountAfter = historyRepository.findAll().size()
      historyCountAfter == historyCountBefore + 1
    })
  }

  @PendingFeature
  def "Alarm Websocket - CUSTOM Alarm with max only and not met - Not triggered"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_REAL_01
    createTestAlarm(AlarmType.CUSTOM, target, null, 10)
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
    createTestAlarm(AlarmType.CUSTOM, target, null, 20)
    def historyCountBefore = historyRepository.findAll().size()

    when:
    plcValueTestFactory.setCurrentValue(target, 25f)

    then:
    conditions.within(DELAY_SEC_CUSTOM_ALARM, {
      def historyCountAfter = historyRepository.findAll().size()
      historyCountAfter == historyCountBefore + 1
    })
  }

  static BlueprintEntity createPredefinedAlarmBlueprint() {
    return BlueprintEntity.builder()
            .name(AlarmType.PREDEFINED.toString())
            .type(BlueprintType.ALARM)
            .description("Predefined blueprint")
            .build()
  }

  static BlueprintEntity createCustomAlarmBlueprint() {
    return BlueprintEntity.builder()
            .name(AlarmType.CUSTOM.toString())
            .type(BlueprintType.ALARM)
            .description("Custom blueprint")
            .build()
  }

  void createTestAlarm(AlarmType alarmType, String address, Double min, Double max) {
    def target = address
    def blueprint
            = blueprintRepository
            .findAllByTypeAndName(BlueprintType.ALARM, alarmType.toString())
            .first()
    def sensorConfig = SensorConfigurationEntity.builder()
            .address(target)
            .blueprint(blueprint)
            .build()
    configurationRepository.save(sensorConfig)
    def condition = AlarmConditionEntity.builder()
            .cron(ONE_SECOND_CRON)
            .isEnabled(true)
            .type(alarmType)
            .min(min)
            .max(max)
            .sensorConfiguration(sensorConfig)
            .timeDelay(1)
            .build()
    conditionRepository.save(condition)
  }
}
