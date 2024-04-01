package com.hbc.pms.core.api;

import static com.hbc.pms.core.api.util.DateTimeUtil.VIETNAM_TIMEZONE;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication(scanBasePackages = "com.hbc.pms.*")
public class CoreApiApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(VIETNAM_TIMEZONE);
    SpringApplication.run(CoreApiApplication.class, args);
  }
}
