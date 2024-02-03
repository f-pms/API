package com.hbc.pms.plc.integration.plc4x;

import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.utils.cache.CachedPlcConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Plc4xConfiguration {
  @Bean
  PlcConnectionManager cachedPlcConnectionManager() {
    return CachedPlcConnectionManager.getBuilder().build();
  }
}
