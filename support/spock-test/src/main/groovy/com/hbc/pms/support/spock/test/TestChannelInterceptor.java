package com.hbc.pms.support.spock.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/** A ChannelInterceptor that caches messages. */
public class TestChannelInterceptor implements ChannelInterceptor {

  private final BlockingQueue<Message<?>> messages = new ArrayBlockingQueue<>(999);

  private final List<String> destinationPatterns = new ArrayList<>();

  private final PathMatcher matcher = new AntPathMatcher();
  private final EventCollector eventCollector;

  public TestChannelInterceptor(EventCollector eventCollector) {
    this.eventCollector = eventCollector;
  }

  public void setIncludedDestinations(String... patterns) {
    this.destinationPatterns.addAll(Arrays.asList(patterns));
  }

  /**
   * @return the next received message or {@code null} if the specified time elapses
   */
  public Message<?> awaitMessage(long timeoutInSeconds) throws InterruptedException {
    return this.messages.poll(timeoutInSeconds, TimeUnit.SECONDS);
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    if (this.destinationPatterns.isEmpty()) {
      this.messages.add(message);
      eventCollector.add(constructTestEvent(message));
    } else {
      StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
      if (headers.getDestination() != null) {
        for (String pattern : this.destinationPatterns) {
          if (this.matcher.match(pattern, headers.getDestination())) {
            this.messages.add(message);
            eventCollector.add(constructTestEvent(message));
            break;
          }
        }
      }
    }
    return message;
  }

  @SneakyThrows
  private TestEvent constructTestEvent(Message<?> message) {
    ObjectMapper objectMapper = new ObjectMapper();
    StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
    String json = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
    TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {};
    HashMap<String, String> parsedObj = objectMapper.readValue(json, typeRef);
    return TestEvent.builder()
        .payload(parsedObj)
        .localDateTime(LocalDateTime.now())
        .topic(headers.getDestination())
        .build();
  }
}
