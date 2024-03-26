package com.hbc.pms.core.api.controller.v1.request.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginCommand {
  @NotNull private String username;
  @NotNull private String password;
}
