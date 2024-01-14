package com.hbc.pms.core.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlcConfiguration {
  @Value("${hbc.plc.url}")
  private String plcUrl;
}
