package com.hbc.pms.core.api.controller.v1.request.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
  private String token;
}
