package com.hbc.pms.core.api.controller.v1.request;

import lombok.Data;

@Data
public class SensorConfigurationRequest {
  private String address;
  private double x;
  private double y;
}
