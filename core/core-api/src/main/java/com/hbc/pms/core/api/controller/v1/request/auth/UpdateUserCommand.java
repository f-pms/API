package com.hbc.pms.core.api.controller.v1.request.auth;

import com.hbc.pms.core.api.constaint.RegexConstraints;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdateUserCommand {
  @Length(min = 3, max = 30)
  private String oldPassword;

  @Length(min = 3, max = 30)
  private String password;

  @Length(min = 3, max = 50)
  private String fullName;

  @Email(regexp = RegexConstraints.MAIL_EXPRESSION)
  private String email;
}
