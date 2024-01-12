package com.hbc.pms.integration.db.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Entity
@Builder
@Data
public class SensorConfiguration {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column
  private String name;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Blueprint blueprint;

  @OneToMany(mappedBy = "sensorConfiguration")
  private Set<SensorConfigurationFigure> sensorConfigurationFigures;
}
