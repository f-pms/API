package com.hbc.pms.integration.db.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Table(name = "sensor_configuration")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class SensorConfiguration extends BaseEntity {

  @Column(name = "name")
  private String name;

  @ManyToOne
  @JoinColumn(name = "blueprint_id", nullable = false)
  private BlueprintEntity blueprint;

  @OneToMany(mappedBy = "sensorConfiguration")
  private Set<SensorConfigurationFigure> sensorConfigurationFigures;
}
