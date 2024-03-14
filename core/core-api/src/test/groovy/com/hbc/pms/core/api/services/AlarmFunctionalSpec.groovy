package com.hbc.pms.core.api.services

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.service.AlarmService
import com.hbc.pms.core.api.support.data.AlarmStore
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.core.model.enums.BlueprintType
import com.hbc.pms.integration.db.entity.AlarmConditionEntity
import com.hbc.pms.integration.db.entity.SensorConfigurationEntity
import com.hbc.pms.integration.db.repository.AlarmConditionRepository
import com.hbc.pms.integration.db.repository.BlueprintRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.util.concurrent.PollingConditions

class AlarmFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  SensorConfigurationRepository configurationRepository

  @Autowired
  AlarmConditionRepository conditionRepository

  @Autowired
  BlueprintRepository blueprintRepository

  @Autowired
  AlarmService alarmService

  @Autowired
  AlarmStore alarmStore

  static final def ONE_SECOND_CRON = "*/1 * * * * *"

  def setup() {
    conditions = new PollingConditions(timeout: 5)
  }

  def "Alarm Websocket - PREDEFINED Alarm with 2s checkInterval and 1s timeDelay not met - Not triggered"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_REAL_03
    def predefinedBlueprint
            = blueprintRepository
            .findAllByTypeAndName(BlueprintType.ALARM, "PREDEFINED")
            .first()
    def sensorConfig = SensorConfigurationEntity.builder()
            .address(target)
            .blueprint(predefinedBlueprint)
            .build()
    configurationRepository.save(sensorConfig)
    def condition = AlarmConditionEntity.builder()
            .cron(ONE_SECOND_CRON)
            .isEnabled(true)
            .type(AlarmType.PREDEFINED)
            .sensorConfiguration(sensorConfig)
            .timeDelay(1)
            .build()
    conditionRepository.save(condition)

    when:
    plcValueTestFactory.setCurrentValue(target, true)

    then:
    //TODO an amount of second before check
    0 * alarmService.createHistories(_)
    // create a condition with 2s and 2s
    // create sensorConfig
    // change sensorConfig value
    // wait till 5s until it not met
  }

  def "Alarm Websocket - PREDEFINED Alarm with 2s checkInterval and 2s timeDelay met - Triggered correctly"() {

  }

  def "Alarm Websocket - CUSTOM Alarm with 2s checkInterval and 2s timeDelay not met - Not triggered"() {


  }

  def "Alarm Websocket - CUSTOM Alarm with 2s checkInterval and 2s timeDelay met - Triggered correctly"() {

  }
}
