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

  def "Monitoring Websocket - Send correct PLC values"() {
    given:
    def target = TestDataFixture.PLC_ADDRESS_REAL_01
    def sensorConfiguration = configurationRepository.findAllByAddress(target).first()

    when: "Set tag to #PLCVal"
    plcValueTestFactory.setCurrentValue(target, PLCVal)

    then: "Received event with value = #val"
    assertPlcTagWithValue(sensorConfiguration.id, val)

    where:
    val  | PLCVal
    "5.0"  | 5f
    "15.0" | 15f
  }

  def "Change plctag config -> send corect"() {

  }

  def "Add new tag -> send correct"() {

  }

  //sai so 1 - 2s coi thu
  //dua ra file khac test alarm gom gui mail + push noti
  //han che dung spy


  def "Alarm Websocket - PREDEFINED Alarm with 2s checkInterval and 2s timeDelay not met - Not triggered"() {

  }

  def "Alarm Websocket - PREDEFINED Alarm with 2s checkInterval and 2s timeDelay met - Triggered correctly"() {

  }

  def "Alarm Websocket - CUSTOM Alarm with 2s checkInterval and 2s timeDelay not met - Not triggered"() {


  }
  def "Alarm Websocket - CUSTOM Alarm with 2s checkInterval and 2s timeDelay met - Triggered correctly"() {

  }


}
