package com.hbc.pms.core.api.controller.v1.request.auth;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdateUserCommand {
  @Length(min = 3, max = 30)
  private String password;
}
