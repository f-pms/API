package com.hbc.pms.core.api.services

import com.hbc.pms.core.api.event.MonitorHandler
import com.hbc.pms.core.api.test.setup.AbstractFunctionalTest
import groovy.util.logging.Slf4j
import org.apache.plc4x.java.api.types.PlcResponseCode
import org.apache.plc4x.java.mock.connection.MockDevice
import org.apache.plc4x.java.spi.messages.utils.ResponseItem
import org.apache.plc4x.java.spi.values.PlcREAL
import org.spockframework.spring.SpringSpy
import org.spockframework.spring.UnwrapAopProxy
import org.springframework.beans.factory.annotation.Autowired

@Slf4j
class TestServiceSpec extends AbstractFunctionalTest{
  @Autowired
  MockDevice mockDevice


  @Autowired
  @SpringSpy
  @UnwrapAopProxy
  MonitorHandler monitorHandler

  def "testSingleSuccessTest"() {
    given:
    mockDevice.read(_ as String) >> new ResponseItem<>(PlcResponseCode.OK, new PlcREAL(30f))
    when:
    1 * 1
    then:
    conditions.within(30) {
      2 * monitorHandler.handle(_)
    }
  }
}
