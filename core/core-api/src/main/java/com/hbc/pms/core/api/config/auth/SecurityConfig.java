package com.hbc.pms.core.api.config.auth;

import static com.hbc.pms.support.auth.AuthConstants.LOGIN_PATH;

import com.hbc.pms.support.auth.JwtAuthFilter;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.AntPathMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private static final String[] WHITE_LIST_URLS = {
    "/swagger-resources**", "/swagger-ui**", "/v3/api-docs/**", LOGIN_PATH
  };
  private final UserDetailsService userDetailsService;
  private final JwtAuthFilter jwtAuthFilter;
  private final PasswordEncoder passwordEncoder;

  @Value("${apiPrefix}")
  private String apiPrefix;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable);
    http.cors(Customizer.withDefaults());
    http.authorizeHttpRequests(
        request -> {
          request
              .requestMatchers(
                  r -> {
                    var path = r.getRequestURI();
                    if (!path.startsWith(apiPrefix)) {
                      return false;
                    }
                    var matcher = new AntPathMatcher();
                    return Arrays.stream(WHITE_LIST_URLS)
                        .noneMatch(url -> matcher.match(apiPrefix + url, path));
                  })
              .authenticated();
          request.anyRequest().permitAll();
        });
    http.authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder);
    return authenticationProvider;
  }
}
