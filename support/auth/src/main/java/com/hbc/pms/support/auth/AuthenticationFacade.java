package com.hbc.pms.support.auth;

public interface AuthenticationFacade {
  DetailedAuthenticationToken getAuthentication();

  boolean hasRole(String role);

  String getUserId();
}
