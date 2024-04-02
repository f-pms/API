package com.hbc.pms.core.api.config;

import com.hbc.pms.support.auth.StompChannelInterceptor;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Value("${hbc.origins}")
  private String allowedOrigins;

  private final StompChannelInterceptor stompChannelInterceptor;

  public WebSocketConfig(StompChannelInterceptor stompChannelInterceptor) {
    this.stompChannelInterceptor = stompChannelInterceptor;
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(stompChannelInterceptor);
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .addEndpoint("/websocket")
        .setAllowedOrigins(Arrays.stream(allowedOrigins.split(",")).toArray(String[]::new));
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
  }
}
