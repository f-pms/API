package com.hbc.pms.support.auth;

import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class DetailedAuthenticationToken extends UsernamePasswordAuthenticationToken {
  private final Map<String, Object> claims;

  public DetailedAuthenticationToken(
      Object principal,
      Map<String, Object> claims,
      Collection<? extends GrantedAuthority> authorities) {
    super(principal, null, authorities);
    this.claims = claims;
  }
}
