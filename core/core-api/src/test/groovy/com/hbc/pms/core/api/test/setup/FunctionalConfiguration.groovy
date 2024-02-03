package com.hbc.pms.core.api.test.setup

import org.apache.plc4x.java.mock.connection.MockConnection
import org.apache.plc4x.java.mock.connection.MockDevice
import org.apache.plc4x.java.utils.cache.CachedPlcConnectionManager
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import spock.mock.DetachedMockFactory

@TestConfiguration
class FunctionalConfiguration {
  def detachedMockFactory = new DetachedMockFactory()

  @Bean
  @Primary
  CachedPlcConnectionManager mockCachedManager(MockDevice mockDevice) {
    def mockUrl = "mock:scraper"
    var cachedPlcConnectionManager = CachedPlcConnectionManager.getBuilder().build()
    cachedPlcConnectionManager.getConnection(mockUrl)
    def connection = (MockConnection) cachedPlcConnectionManager.connectionContainers.get(mockUrl).connection
    connection.setDevice(mockDevice)
    return cachedPlcConnectionManager
  }

  @Bean
  MockDevice mockDevice() {
    return detachedMockFactory.Mock(MockDevice)
  }
}
