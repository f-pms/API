package com.hbc.pms.support.spock.test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public interface EventCollector {

  Map<String, List<TestEvent>> getAllEvents();

  void add(TestEvent testEvent);

  void clear();

  Optional<TestEvent> getEvent(Predicate<TestEvent> predicate);

  Optional<TestEvent> getEvent(String topic, Predicate<TestEvent> predicate);

  int size();
}
