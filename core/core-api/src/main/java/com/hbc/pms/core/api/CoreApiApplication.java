package com.hbc.pms.core.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConfigurationPropertiesScan
@SpringBootApplication(scanBasePackages = "com.hbc.pms.*")
@EnableScheduling
public class CoreApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(CoreApiApplication.class, args);
  }
}
