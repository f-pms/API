package com.hbc.pms.integration.db.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Builder
@Data
public class SensorConfigurationFigure {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column
  private String name;

  @Column
  private String dataType;

  @Column
  private int dataBlockNumber;

  @Column
  private int offset;

  @Column
  private int x;

  @Column
  private int y;

  @ManyToOne
  @JoinColumn(nullable = false)
  private SensorConfiguration sensorConfiguration;
}
