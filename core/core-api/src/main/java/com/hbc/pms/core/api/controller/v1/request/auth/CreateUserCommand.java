package com.hbc.pms.core.api.controller.v1.request.auth;

import com.hbc.pms.core.api.constaint.RegexConstraints;
import com.hbc.pms.core.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class CreateUserCommand {
  @NotNull
  @Length(min = 3, max = 50)
  @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Field must contain only a-z, A-Z, 0-9 characters")
  private String username;

  @Length(min = 3, max = 30)
  private String password;

  @NotNull
  @Length(min = 3, max = 50)
  private String fullName;

  @NotNull
  @Email(regexp = RegexConstraints.MAIL_EXPRESSION)
  private String email;

  @NotNull private Role role;
}
