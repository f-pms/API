package com.hbc.pms.plc.io;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IoDetailsConfiguration {
  @Bean
  public IoDetailsService ioDetailsService(JsonIoDetailsRepository jsonIoDetailsRepository) {
    return new StaticIoDetailsService(jsonIoDetailsRepository);
  }
}
