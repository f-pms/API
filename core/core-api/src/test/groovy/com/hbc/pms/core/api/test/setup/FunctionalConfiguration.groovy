package com.hbc.pms.core.api.test.setup


import org.springframework.boot.test.context.TestConfiguration
import spock.mock.DetachedMockFactory

@TestConfiguration
class FunctionalConfiguration {
  def detachedMockFactory = new DetachedMockFactory()

}
