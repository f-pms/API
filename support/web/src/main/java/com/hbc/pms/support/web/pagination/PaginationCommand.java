package com.hbc.pms.support.web.pagination;

import lombok.Data;
import org.springframework.data.domain.PageRequest;

@Data
public class PaginationCommand {
  protected int page;
  protected int size;

  public PageRequest toPageable() {
    return PageRequest.of(page, size);
  }
}
