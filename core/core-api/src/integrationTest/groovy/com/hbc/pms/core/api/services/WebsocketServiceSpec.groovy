package com.hbc.pms.core.api.services

import com.hbc.pms.core.api.IntegrationTestSpec
import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

@Slf4j
class WebsocketServiceSpec extends IntegrationTestSpec {
  @Autowired
  SensorConfigurationRepository configurationRepository

  def "Websocket - Send correct PLC values"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_REAL_01
    def sensorConfiguration = configurationRepository.findAllByAddress(target).first()

    when: "Set tag to #val"
    plcValueTestFactory.setCurrentValue(target, val)

    then: "Received event with value = #val"
    assertPlcTagWithValue(sensorConfiguration.id, val.toString())

    where:
    val << [5.0, 15.0]
  }

}