package com.hbc.pms.core.api.config.mapper;

import lombok.experimental.UtilityClass;
import org.modelmapper.Condition;

@UtilityClass
public class MapperConditions {
  public static Condition<Object, Object> mapIfSourceNotNull() {
    return mappingContext -> mappingContext.getSource() != null;
  }
}
