package com.hbc.pms.core.model.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hbc.pms.core.model.enums.Role;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public abstract class UserMixin {
  @JsonProperty private Long id;
  @JsonProperty private String username;
  @JsonProperty private String fullName;
  @JsonProperty private String email;
  @JsonProperty private Role role;
  @JsonIgnore private String password;

  @JsonIgnore
  abstract Collection<? extends GrantedAuthority> getAuthorities();

  @JsonIgnore
  abstract boolean isAccountNonExpired();

  @JsonIgnore
  abstract boolean isAccountNonLocked();

  @JsonIgnore
  abstract boolean isCredentialsNonExpired();

  @JsonIgnore
  abstract boolean isEnabled();
}
