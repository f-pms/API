package com.hbc.pms.plc.api;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.Map;

@Getter
@Setter
@Builder
public class PlcConfiguration {
  @Singular
  private Map<String, String> deviceConnections;
}
