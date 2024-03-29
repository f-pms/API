package com.hbc.pms.support.auth;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DefaultAuthenticationFacade implements AuthenticationFacade {
  @Override
  public DetailedAuthenticationToken getAuthentication() {
    return (DetailedAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
  }

  public Set<String> getRoles() {
    return getAuthentication().getAuthorities().stream()
        .map(grantedAuthority -> grantedAuthority.getAuthority().split("_")[1])
        .collect(Collectors.toSet());
  }

  public boolean hasRole(String role) {
    return getRoles().contains(role);
  }

  public String getUserId() {
    return getAuthentication().getClaims().get(AuthConstants.USER_ID).toString();
  }
}
