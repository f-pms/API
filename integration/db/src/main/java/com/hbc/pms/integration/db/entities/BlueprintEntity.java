package com.hbc.pms.integration.db.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Table(name = "blueprint")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class BlueprintEntity extends BaseEntity {

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

  @OneToMany(mappedBy = "blueprint")
  private Set<SensorConfiguration> sensorConfigurations;
}
