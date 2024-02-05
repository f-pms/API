package com.hbc.pms.integration.db.entity;

import com.hbc.pms.core.model.enums.AlarmSeverity;
import com.hbc.pms.core.model.enums.AlarmType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  @Column private String name;

  @Column private boolean isEnabled; // TODO: will implement

  @Column
  @Enumerated(EnumType.STRING)
  private AlarmSeverity severity;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private SensorConfigurationEntity sensorConfiguration;

  @Column
  @Enumerated(EnumType.STRING)
  private AlarmType type;

  @Column private String cron;

  @Column private int timeDelay;

  @Column private Double min;

  @Column private Double max;

  @OneToMany(mappedBy = "condition", fetch = FetchType.EAGER)
  private List<AlarmActionEntity> actions;
}
