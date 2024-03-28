package com.hbc.pms.support.web.pagination;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@Builder
public class QueryResult<T> {
  private final long pageTotal;
  private final long recordTotal;
  private final List<T> content;

  public static <T> QueryResult<T> fromPage(Page<T> page) {
    return QueryResult.<T>builder()
        .pageTotal(page.getTotalPages())
        .recordTotal(page.getTotalElements())
        .content(page.getContent())
        .build();
  }
}
