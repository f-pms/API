package com.hbc.pms.integration.db.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "com.hbc.pms.integration.db")
@EnableJpaRepositories(basePackages = "com.hbc.pms.integration.db")
class CoreJpaConfig {}
