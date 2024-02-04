package com.hbc.pms.integration.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "sensor_configuration")
@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SensorConfigurationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String address;

  @Column
  private Double x;

  @Column
  private Double y;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private BlueprintEntity blueprint;

}
