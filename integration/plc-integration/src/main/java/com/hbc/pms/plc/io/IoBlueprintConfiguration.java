package com.hbc.pms.plc.io;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IoBlueprintConfiguration {
  @Bean
  public IoBlueprintService ioBlueprintService(JsonIoBlueprintRepository jsonIoDetailsRepository) {
    return new StaticIoBlueprintService(jsonIoDetailsRepository);
  }
}
