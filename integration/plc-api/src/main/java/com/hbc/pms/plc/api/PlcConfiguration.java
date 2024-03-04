package com.hbc.pms.plc.api;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
@Builder
public class PlcConfiguration {

  @Singular private Map<String, String> deviceConnections;
}
