package com.hbc.pms.core.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor()
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SensorConfiguration {
  private Long id;
  private String address;
  private double x;
  private double y;
}
