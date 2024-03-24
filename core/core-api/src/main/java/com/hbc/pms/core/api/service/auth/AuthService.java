package com.hbc.pms.core.api.service.auth;

import com.hbc.pms.core.api.controller.v1.request.auth.AuthResponse;
import com.hbc.pms.core.api.controller.v1.request.auth.LoginCommand;
import com.hbc.pms.core.model.User;
import com.hbc.pms.support.auth.AuthConstants;
import com.hbc.pms.support.auth.JwtService;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthService {
  private final AuthenticationManager authenticateManager;
  private final UserPersistenceService userPersistenceService;
  private final JwtService jwtService;

  public AuthResponse authenticate(LoginCommand loginCommand) {
    authenticateManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginCommand.getUsername(), loginCommand.getPassword()));

    User target = userPersistenceService.findByUsername(loginCommand.getUsername());
    return new AuthResponse(jwtService.generateToken(buildClaimsFromUser(target), target));
  }

  private HashMap<String, Object> buildClaimsFromUser(User target) {
    HashMap<String, Object> claims = new HashMap<>();
    claims.put(AuthConstants.USER_ID, target.getId());
    return claims;
  }
}
