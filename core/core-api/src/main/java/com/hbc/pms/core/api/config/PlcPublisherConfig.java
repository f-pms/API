package com.hbc.pms.core.api.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "hbc.plc.publisher.enabled", matchIfMissing = true)
public class PlcPublisherConfig {
}
