package com.hbc.pms.core.api.services

import com.hbc.pms.core.api.FunctionalTestSpec
import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.service.blueprint.SensorConfigurationPersistenceService
import com.hbc.pms.core.model.SensorConfiguration
import com.hbc.pms.integration.db.repository.BlueprintRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import com.hbc.pms.plc.api.PlcConnector
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

@Slf4j
class MonitoringFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  SensorConfigurationRepository configurationRepository

  @Autowired
  SensorConfigurationPersistenceService configurationPersistenceService

  @Autowired
  BlueprintRepository blueprintRepository

  @Autowired
  PlcConnector connector

  Long SENSOR_CONFIG_ID

  def setup() {
    def blueprint
            = blueprintRepository.findById(TestDataFixture.MONITORING_BLUEPRINT_ID).get()
    SENSOR_CONFIG_ID = configurationRepository
            .save(TestDataFixture.createSensorConfiguration(blueprint, TestDataFixture.PLC_ADDRESS_REAL_01)).getId()
    connector.updateScheduler()
  }

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

  def "Monitoring Websocket - Update PLC Tag then monitor - Sent correct PLC values"() {
    given:
    def sensorConfigEntity
            = configurationRepository.findAllByAddress(TestDataFixture.PLC_ADDRESS_REAL_01).first()
    def sensorConfig
            = configurationPersistenceService.get(sensorConfigEntity.id)

    when:
    "Set tag to $TestDataFixture.PLC_ADDRESS_REAL_02"
    String target = TestDataFixture.PLC_ADDRESS_REAL_02
    sensorConfig.setAddress(target)
    configurationPersistenceService.update(sensorConfigEntity.getBlueprint().getId(), sensorConfig)
    plcValueTestFactory.setCurrentValue(target, 5f)

    then: "Received event with value = #val"
    assertPlcTagWithValue(sensorConfig.id, "5.0")
  }

  def "Monitoring Websocket - Add new PLC Tag then monitor - Sent correct PLC values"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_REAL_03
    def sensorConfig
            = SensorConfiguration.builder()
            .address(target)
            .build()
    configurationPersistenceService.create(TestDataFixture.MONITORING_BLUEPRINT_ID, sensorConfig)

    //TODO: workaround until changing the return value of the create method
    def createdSensorConfig
            = configurationRepository.findAllByAddress(target).first()

    when:
    plcValueTestFactory.setCurrentValue(target, 5f)

    then: "Received event with value = #val"
    assertPlcTagWithValue(createdSensorConfig.id, "5.0")
  }
}
