package com.hbc.pms.core.api.domain;

import org.springframework.context.ApplicationEvent;

public class Event extends ApplicationEvent {

  public Event(Object source) {
    super(source);
  }
}
