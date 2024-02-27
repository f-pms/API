package com.hbc.pms.core.api.controller.v1.response;

import lombok.Data;

@Data
public class SensorConfigurationResponse {
  private Long id;
  private String address;
  private double x;
  private double y;
  private boolean attachedToAlarm = false;
}
