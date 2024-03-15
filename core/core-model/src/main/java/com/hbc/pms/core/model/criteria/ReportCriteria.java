package com.hbc.pms.core.model.criteria;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ReportCriteria {
  private Long reportTypeId;

  // set min, max sql server datetime
  // https://learn.microsoft.com/en-us/sql/t-sql/data-types/datetimeoffset-transact-sql
  @Builder.Default
  private OffsetDateTime startDate = OffsetDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
  @Builder.Default
  private OffsetDateTime endDate = OffsetDateTime.of(9999, 12, 31, 23, 59, 0, 0, ZoneOffset.UTC);
}
