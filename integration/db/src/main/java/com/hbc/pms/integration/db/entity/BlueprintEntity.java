package com.hbc.pms.integration.db.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Entity
@Builder
@Data
public class BlueprintEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column
  private String name;

  @Column
  private String description;

  @OneToMany(mappedBy = "blueprint")
  private Set<SensorConfigurationEntity> sensorConfigurations;
}
