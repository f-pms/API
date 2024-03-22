package com.hbc.pms.core.api.service.auth;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserService implements UserDetailsService {
  private final UserPersistenceService userPersistenceService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userPersistenceService.findByUsername(username);
  }
}
