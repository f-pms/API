package com.hbc.pms.core.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SensorConfiguration {
  private Long id;
  private String address;
  private double x;
  private double y;
}
