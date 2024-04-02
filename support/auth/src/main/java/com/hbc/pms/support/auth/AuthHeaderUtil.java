package com.hbc.pms.support.auth;

import static com.hbc.pms.support.auth.AuthConstants.AUTHORIZATION_HEADER;
import static com.hbc.pms.support.auth.JwtAuthFilter.AUTH_COOKIE_NAME;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthHeaderUtil {
  public static Optional<String> tryToExtractTokenFromHeader(String authHeader) {
    Optional<String> jwt = Optional.empty();
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      jwt = Optional.of(authHeader.substring(7));
    }
    return jwt;
  }

  public static Optional<String> tryToExtractTokenFromCookie(Cookie[] cookies) {
    Optional<String> jwt = Optional.empty();
    if (cookies != null) {
      Optional<Cookie> authCookie =
          Arrays.stream(cookies)
              .filter(cookie -> cookie.getName().equals(AUTH_COOKIE_NAME))
              .findFirst();
      if (authCookie.isPresent()) {
        jwt = Optional.of(authCookie.get().getValue());
      }
    }
    return jwt;
  }

  public static Optional<String> tryToExtractToken(HttpServletRequest request) {
    return tryToExtractTokenFromCookie(request.getCookies())
        .or(() -> tryToExtractTokenFromHeader(request.getHeader(AUTHORIZATION_HEADER)));
  }
}
