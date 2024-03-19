package com.hbc.pms.core.api.services

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.core.model.enums.AlarmStatus
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.integration.db.repository.*
import com.hbc.pms.plc.api.PlcConnector
import org.springframework.beans.factory.annotation.Autowired

class AlarmResolverFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  BlueprintRepository blueprintRepository

  @Autowired
  SensorConfigurationRepository configurationRepository

  @Autowired
  AlarmConditionRepository conditionRepository

  @Autowired
  AlarmHistoryRepository historyRepository

  @Autowired
  PlcConnector connector

  @Autowired
  AlarmActionRepository actionRepository

  Long CONDITION_ID
  int ALLOWED_DELAY_SEC = 8

  def setup() {
    def blueprint
            = blueprintRepository.findById(TestDataFixture.PREDEFINED_ALARM_BLUEPRINT_ID).get()
    def sensorConfig
            = configurationRepository
            .save(
                    TestDataFixture.createSensorConfiguration(blueprint, TestDataFixture.PLC_ADDRESS_BOOL_01)
            )
    def condition
            = conditionRepository
            .save(TestDataFixture.createDefaultConditionEntity(AlarmType.PREDEFINED, sensorConfig))
    CONDITION_ID = condition.getId()
    actionRepository.deleteAll()
    connector.updateScheduler()

  }

  def "Alarm Resolver - Condition still met - Done nothing"() {
    given:
    def condition = conditionRepository.findById(CONDITION_ID).get()
    def history
            = historyRepository.save(TestDataFixture.createHistory(condition, AlarmStatus.TRIGGERED))

    when:
    plcValueTestFactory.setCurrentValue(TestDataFixture.PLC_ADDRESS_BOOL_01, true)

    then:
    Thread.sleep(ALLOWED_DELAY_SEC * 1000)
    //TODO: should use entityManager.refresh()
    def theHistory = historyRepository.findById(history.getId()).get()

    theHistory.status == AlarmStatus.TRIGGERED || theHistory.status == AlarmStatus.SENT
  }

  def "Alarm Resolver - Condition is not met anymore - Change from SENT to SOLVED"() {
    given:
    def condition = conditionRepository.findById(CONDITION_ID).get()
    def history
            = historyRepository.save(TestDataFixture.createHistory(condition, AlarmStatus.SENT))

    when:
    plcValueTestFactory.setCurrentValue(TestDataFixture.PLC_ADDRESS_BOOL_01, false)

    then:
    Thread.sleep(ALLOWED_DELAY_SEC * 1000)
    //TODO: should use entityManager.refresh()
    def theHistory = historyRepository.findById(history.getId()).get()

    theHistory.status == AlarmStatus.SOLVED
  }

  def "Alarm Resolver - Condition is not met anymore - Change from TRIGGERED to SOLVED"() {
    given:
    def condition = conditionRepository.findById(CONDITION_ID).get()
    def history
            = historyRepository.save(TestDataFixture.createHistory(condition, AlarmStatus.TRIGGERED))

    when:
    plcValueTestFactory.setCurrentValue(TestDataFixture.PLC_ADDRESS_BOOL_01, false)

    then:
    Thread.sleep(ALLOWED_DELAY_SEC * 1000)
    //TODO: should use entityManager.refresh()
    def theHistory = historyRepository.findById(history.getId()).get()

    theHistory.status == AlarmStatus.SOLVED
  }
}
