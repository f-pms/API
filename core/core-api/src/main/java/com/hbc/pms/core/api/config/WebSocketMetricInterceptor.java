package com.hbc.pms.core.api.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class WebSocketMetricInterceptor implements ChannelInterceptor {

  private final Map<String, Integer> connectedSessionIds;

  public WebSocketMetricInterceptor() {
    this.connectedSessionIds = new ConcurrentHashMap<>();
  }

  public int countSubscriberOfTopic(String topic) {
    return connectedSessionIds.getOrDefault("/topic/" + topic, 0);
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    var accessor = StompHeaderAccessor.wrap(message);
    var command = accessor.getCommand();
    var sessionId = accessor.getSessionId();
    var topic = accessor.getDestination();
    if (sessionId == null
        || (command != StompCommand.SUBSCRIBE && command != StompCommand.UNSUBSCRIBE)) {
      return message;
    }

    if (!connectedSessionIds.containsKey(topic)) {
      connectedSessionIds.put(topic, 0);
    }
    int current = connectedSessionIds.get(topic);
    if (command == StompCommand.SUBSCRIBE) {
      current++;
    } else {
      current--;
    }
    connectedSessionIds.put(topic, Math.max(current, 0));
    return message;
  }
}
