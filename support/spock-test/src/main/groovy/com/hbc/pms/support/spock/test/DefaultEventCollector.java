package com.hbc.pms.support.spock.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Getter;

@Getter
public class DefaultEventCollector implements EventCollector {

  private final Map<String, List<TestEvent>> events = new HashMap<>();

  @Override
  public Map<String, List<TestEvent>> getAllEvents() {
    return events;
  }

  @Override
  public void add(TestEvent testEvent) {
    String topic = testEvent.getTopic();
    if (!events.containsKey(topic)) {
      events.put(topic, new ArrayList<>());
    }
    events.get(topic).add(testEvent);
  }

  @Override
  public void clear() {
    events.clear();
  }

  @Override
  public Optional<TestEvent> getEvent(Predicate<TestEvent> predicate) {
    return events.values().stream().flatMap(Collection::stream).filter(predicate).findAny();
  }

  @Override
  public Optional<TestEvent> getEvent(String topic, Predicate<TestEvent> predicate) {
    return events.get(topic).stream().filter(predicate).findAny();
  }

  @Override
  public int size() {
    return events.values().stream().flatMap(Collection::stream).toList().size();
  }
}
