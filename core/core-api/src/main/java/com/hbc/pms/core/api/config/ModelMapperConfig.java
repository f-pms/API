package com.hbc.pms.core.api.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper
        .getConfiguration()
        .setMatchingStrategy(MatchingStrategies.STANDARD)
        .setSkipNullEnabled(true)
        .setDeepCopyEnabled(true);
    return modelMapper;
  }
}
