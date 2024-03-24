package com.hbc.pms.core.api.controller.v1.request.auth;

import com.hbc.pms.core.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateUserCommand {
  private String username;
  private String password;
  private Role role;
}
