package com.hbc.pms.integration.db.entity;

import com.hbc.pms.core.model.enums.AlarmSeverityEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "alarm_condition")
@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AlarmConditionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String name;

  @Column
  private String description;

  @Column
  private String cron;

  @Column
  private int timeDelay;

  @Column
  private boolean isEnabled;

  @Column
  private Double min;

  @Column
  private Double max;

  @Column
  @Enumerated(EnumType.STRING)
  private AlarmSeverityEnum severity;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private SensorConfigurationEntity sensorConfiguration;
}
