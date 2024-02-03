package com.hbc.pms.core.api.services

import com.hbc.pms.core.api.test.setup.AbstractFunctionalTest
import groovy.util.logging.Slf4j

@Slf4j
class TestServiceSpec extends AbstractFunctionalTest{
  def "testSingleSuccessTest"() {
    expect:
    log.info("Success")
  }
}
