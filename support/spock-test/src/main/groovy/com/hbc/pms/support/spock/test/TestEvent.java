package com.hbc.pms.support.spock.test;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TestEvent {
  private String topic;
  private Map<String, String> payload;
  private LocalDateTime localDateTime;
}
