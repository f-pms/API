package com.hbc.pms.core.api.controller.v1;

import static com.hbc.pms.support.auth.JwtAuthFilter.AUTH_COOKIE_NAME;

import com.hbc.pms.core.api.controller.v1.request.auth.AuthResponse;
import com.hbc.pms.core.api.controller.v1.request.auth.LoginCommand;
import com.hbc.pms.core.api.service.auth.AuthService;
import com.hbc.pms.support.web.response.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  public ApiResponse<AuthResponse> login(
      @RequestBody LoginCommand loginCommand, HttpServletResponse httpServletResponse) {
    AuthResponse authResponse = authService.authenticate(loginCommand);
    Cookie authCookie = new Cookie(AUTH_COOKIE_NAME, authResponse.getToken());
    authCookie.setPath("/");
    httpServletResponse.addCookie(authCookie);
    return ApiResponse.success(authResponse);
  }
}
