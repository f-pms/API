package com.hbc.pms.support.spock.test

import org.apache.plc4x.java.api.exceptions.PlcConnectionException
import org.apache.plc4x.java.mock.connection.MockConnection
import org.apache.plc4x.java.mock.connection.MockDevice
import org.apache.plc4x.java.utils.cache.CachedPlcConnectionManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import spock.mock.DetachedMockFactory

@Configuration
@ComponentScan
class SpockTestAutoConfiguration {
  def detachedMockFactory = new DetachedMockFactory()

  @Bean
  @Primary
  CachedPlcConnectionManager mockCachedManager(MockDevice mockDevice) throws PlcConnectionException {
    String mockUrl = "mock:scraper";
    var cachedPlcConnectionManager = CachedPlcConnectionManager.getBuilder().build();
    cachedPlcConnectionManager.getConnection(mockUrl);
    var connection = (MockConnection) cachedPlcConnectionManager.connectionContainers.get(mockUrl).connection;
    connection.setDevice(mockDevice);
    return cachedPlcConnectionManager;
  }

  @Bean
  MockDevice mockDevice() {
    return detachedMockFactory.Stub(MockDevice)
  }

  @Bean
  EventCollector eventCollector() {
    return new DefaultEventCollector()
  }

//  @Bean
//  @Primary
//  SimpMessagingTemplate simpMessagingTemplate() {
//    return detachedMockFactory.Spy(simpMessagingTemplate)
//  }
}
