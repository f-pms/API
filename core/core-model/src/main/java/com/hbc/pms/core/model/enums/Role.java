package com.hbc.pms.core.model.enums;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum Role {
  @FieldNameConstants.Include
  ADMIN,
  @FieldNameConstants.Include
  SUPERVISOR
}
