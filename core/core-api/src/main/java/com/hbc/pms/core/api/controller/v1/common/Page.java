package com.hbc.pms.core.api.controller.v1.common;

import lombok.Data;

@Data
public class Page<T> {
  private final int total;
  private final T content;
}
