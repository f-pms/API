package com.hbc.pms.core.model;

import com.hbc.pms.core.model.mixin.UserMixin;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@AllArgsConstructor
public class CoreModelAutoconfiguration {
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer personCustomizer() {
    return jacksonObjectMapperBuilder -> {
      jacksonObjectMapperBuilder.mixIn(User.class, UserMixin.class);
    };
  }
}
