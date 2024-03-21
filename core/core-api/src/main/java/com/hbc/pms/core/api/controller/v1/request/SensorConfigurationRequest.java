package com.hbc.pms.core.api.controller.v1.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SensorConfigurationRequest {
  private String address;
  private double x;
  private double y;
}
