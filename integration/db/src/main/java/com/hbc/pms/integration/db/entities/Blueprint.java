package com.hbc.pms.integration.db.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Entity
@Builder
@Data
public class Blueprint {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column
  private String name;

  @Column
  private String description;

  @OneToMany(mappedBy = "blueprint")
  private Set<SensorConfiguration> sensorConfigurations;
}
