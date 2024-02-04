package com.hbc.pms.integration.db.entity;

import com.hbc.pms.core.model.enums.AlarmType;
import com.hbc.pms.core.model.enums.BlueprintType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

  @Column
  @Enumerated(EnumType.STRING)
  private BlueprintType type;
}
