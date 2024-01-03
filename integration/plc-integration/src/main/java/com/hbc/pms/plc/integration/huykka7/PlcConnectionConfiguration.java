package com.hbc.pms.plc.integration.huykka7;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlcConnectionConfiguration {
  private String ipAddress;
  private int rack;
  private int cpuMpiAddress;

  @Builder.Default
  private int port = 102;
}
