package com.hbc.pms.core.api.controller.v1.request.auth;

import lombok.Data;

@Data
public class UpdateUserCommand {
  private String password;
}
