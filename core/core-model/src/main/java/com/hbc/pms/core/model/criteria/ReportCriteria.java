package com.hbc.pms.core.model.criteria;

import com.hbc.pms.core.model.enums.ReportOrder;
import com.hbc.pms.core.model.enums.ReportSort;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
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
  @Builder.Default private List<Long> typeIds = List.of();
  @Builder.Default private List<Long> ids = List.of();

  // set min sql server datetime
  // https://learn.microsoft.com/en-us/sql/t-sql/data-types/datetimeoffset-transact-sql
  @Builder.Default
  private OffsetDateTime startDate = OffsetDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

  // set max sql server datetime
  // https://learn.microsoft.com/en-us/sql/t-sql/data-types/datetimeoffset-transact-sql
  @Builder.Default
  private OffsetDateTime endDate = OffsetDateTime.of(9999, 12, 31, 23, 59, 0, 0, ZoneOffset.UTC);

  @Builder.Default private ReportSort sortBy = ReportSort.NONE;

  @Builder.Default private ReportOrder order = ReportOrder.ASC;
}
