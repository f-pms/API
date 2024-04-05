package com.hbc.pms.support.auth;

import static com.hbc.pms.support.auth.AuthHeaderUtil.tryToExtractToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
  public static final String AUTH_COOKIE_NAME = "ACCESS_TOKEN";
  private final JwtService jwtService;
  private final UserDetailsService userService;
  private final HandlerExceptionResolver handlerExceptionResolver;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    Optional<String> jwtOptional = tryToExtractToken(request);

    if (jwtOptional.isPresent()) {
      try {
        String jwt = jwtOptional.get();
        String userEmail = jwtService.extractUsername(jwt);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          UserDetails userDetails = this.userService.loadUserByUsername(userEmail);
          if (!jwtService.isTokenNotValid(jwt, userDetails)) {
            DetailedAuthenticationToken authToken =
                new DetailedAuthenticationToken(
                    userDetails, jwtService.extractAllClaims(jwt), userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
          }
        }
      } catch (Exception exception) {
        handlerExceptionResolver.resolveException(request, response, null, exception);
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}
