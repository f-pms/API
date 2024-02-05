package com.hbc.pms.core.model;

import com.hbc.pms.core.model.enums.AlarmActionType;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AlarmAction {

  private Long id;
  private AlarmActionType type;
  private String message;
  private Set<String> recipients;
}
