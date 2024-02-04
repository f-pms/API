package com.hbc.pms.integration.db.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "blueprint")
@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class BlueprintEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String name;

  @Column
  private String description;

  @OneToMany(mappedBy = "blueprint", fetch = FetchType.EAGER)
  private List<SensorConfigurationEntity> sensorConfigurations;

}
