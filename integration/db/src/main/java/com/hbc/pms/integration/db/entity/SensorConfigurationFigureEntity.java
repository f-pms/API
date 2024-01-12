package com.hbc.pms.integration.db.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sensor_configuration_figure")
@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SensorConfigurationFigureEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String address;

  @Column
  private double x;

  @Column
  private double y;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private SensorConfigurationEntity sensorConfiguration;
}
