package com.hbc.pms.support.web.pagination;

import lombok.Data;
import org.springframework.data.domain.PageRequest;

@Data
public class PaginationCommand {
  protected int page = 1;
  protected int size = 10;

  public PageRequest toPageable() {
    return PageRequest.of(page - 1, size);
  }
}
