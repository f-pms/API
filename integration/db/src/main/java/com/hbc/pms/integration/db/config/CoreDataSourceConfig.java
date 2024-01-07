package com.hbc.pms.integration.db.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CoreDataSourceConfig {

  @Bean
  @ConfigurationProperties(prefix = "integration.datasource.core")
  public HikariConfig coreHikariConfig() {
    return new HikariConfig();
  }

  @Bean
  public HikariDataSource coreDataSource(@Qualifier("coreHikariConfig") HikariConfig config) {
    return new HikariDataSource(config);
  }

}
