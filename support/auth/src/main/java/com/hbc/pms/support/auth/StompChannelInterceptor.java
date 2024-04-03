package com.hbc.pms.support.auth;

import static com.hbc.pms.support.auth.AuthConstants.AUTHORIZATION_HEADER;

import com.hbc.pms.support.web.error.CoreApiException;
import com.hbc.pms.support.web.error.ErrorType;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class StompChannelInterceptor implements ChannelInterceptor {
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    boolean isAuthenticated = false;
    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
      final Optional<String> tokenOptional =
          AuthHeaderUtil.tryToExtractTokenFromHeader(
              accessor.getFirstNativeHeader(AUTHORIZATION_HEADER));
      if (tokenOptional.isPresent()) {
        try {
          String token = tokenOptional.get();
          String userEmail = jwtService.extractUsername(token);
          UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
          if (!jwtService.isTokenNotValid(token, userDetails)) {
            DetailedAuthenticationToken authToken =
                new DetailedAuthenticationToken(
                    userDetails, jwtService.extractAllClaims(token), userDetails.getAuthorities());
            accessor.setUser(authToken);
            isAuthenticated = true;
          }
        } catch (Exception e) {
          log.warn("Error while trying to authenticate user: {}", e.getMessage());
        }
      }
      if (!isAuthenticated) {
        throw new CoreApiException(ErrorType.FORBIDDEN_ERROR);
      }
    }
    return message;
  }
}
