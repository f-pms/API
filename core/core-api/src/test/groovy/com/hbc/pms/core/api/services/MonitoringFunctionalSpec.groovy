package com.hbc.pms.core.api.services

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.service.SensorConfigurationPersistenceService
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.core.model.SensorConfiguration
import com.hbc.pms.integration.db.repository.BlueprintRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore

@Slf4j
class MonitoringFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  SensorConfigurationRepository configurationRepository

  @Autowired
  BlueprintRepository blueprintRepository

  @Autowired
  SensorConfigurationPersistenceService configurationPersistenceService

  def "Monitoring Websocket - Send correct PLC values"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_REAL_01
    def sensorConfiguration = configurationRepository.findAllByAddress(target).first()

    when: "Set tag to #PLCVal"
    plcValueTestFactory.setCurrentValue(target, PLCVal)

    then: "Received event with value = #val"
    assertPlcTagWithValue(sensorConfiguration.id, val)

    where:
    val    | PLCVal
    "5.0"  | 5f
    "15.0" | 15f
  }

  //TODO: need to update the Service's implementation, then test Service not Repository
  @Ignore
  def "Monitoring Websocket - Update PLC Tag then monitor - Sent correct PLC values"() {
    given:
    def sensorConfig
            = configurationRepository.findAllByAddress(TestDataFixture.PLC_ADDRESS_REAL_01).first()

    when:
    "Set tag to $TestDataFixture.PLC_ADDRESS_REAL_02"
    String target = TestDataFixture.PLC_ADDRESS_REAL_02
    sensorConfig.setAddress(target)
    configurationRepository.save(sensorConfig)
    plcValueTestFactory.setCurrentValue(target, 5f)

    then: "Received event with value = #val"
    assertPlcTagWithValue(sensorConfig.id, "5.0")
  }

  //TODO: need to update the Service's implementation, then test Service not Repository
  @Ignore
  def "Monitoring Websocket - Add new PLC Tag then monitor - Sent correct PLC values"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_REAL_03
    def blueprint = blueprintRepository.findAll().first()
    def sensorConfig
            = SensorConfiguration.builder()
            .address(target)
            .build()
    configurationPersistenceService.create(blueprint.id, sensorConfig)

    when:
    plcValueTestFactory.setCurrentValue(target, 5f)

    then: "Received event with value = #val"
    assertPlcTagWithValue(sensorConfig.id, "5.0")
  }
}
