package com.hbc.pms.core.api.controller.v1.common;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Page<T> {
  private final long pageTotal;
  private final long recordTotal;
  private final List<T> content;
}
