package com.hbc.pms.core.api.services

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

@Slf4j
class WebsocketServiceSpec extends FunctionalTestSpec {
  @Autowired
  SensorConfigurationRepository configurationRepository

  def "Websocket sends correct PLC values"() {
    def target = TestDataFixture.PLC_ADDRESS_REAL_01
    def sensorConfiguration = configurationRepository.findAllByAddress(target).first()
    when: "Set tag to 5f"
    plcValueTestFactory.setCurrentValue(target, 5f)

    then: "Received event with value = 5.0"
    assertPlcTagWithValue(sensorConfiguration.id, "5.0")

    when: "Set tag to 15f"
    plcValueTestFactory.setCurrentValue(target, 15f)

    then: "Received event with value = 15.0"
    assertPlcTagWithValue(sensorConfiguration.id, "15.0")
  }

}
