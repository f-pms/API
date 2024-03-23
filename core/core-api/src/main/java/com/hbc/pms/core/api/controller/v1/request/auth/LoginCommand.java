package com.hbc.pms.core.api.controller.v1.request.auth;

import lombok.Data;

@Data
public class LoginCommand {
  private String username;
  private String password;
}
