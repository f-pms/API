package com.hbc.pms.core.api.config;

import com.hbc.pms.plc.integration.huykka7.PlcConnectionConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlcConfiguration {
  @Value("${hbc.plc.url}")
  private String plcUrl;

  @Bean
  PlcConnectionConfiguration plcConnectionConfiguration(){
    return PlcConnectionConfiguration.builder()
        .ipAddress(plcUrl)
        .rack(0)
        .cpuMpiAddress(1)
        .build();
  }
}
