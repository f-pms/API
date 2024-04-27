package com.hbc.pms.core.api.config.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "hbc.root")
@Getter
@Setter
@Validated
public class RootUserProperties {
  @NotNull
  @Size(min = 3, max = 25)
  private String username;

  @Size(min = 3, max = 25)
  @NotNull
  private String password;
}
