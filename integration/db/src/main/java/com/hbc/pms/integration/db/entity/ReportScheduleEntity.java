package com.hbc.pms.integration.db.entity;

import com.hbc.pms.core.model.enums.ReportRowPeriod;
import com.hbc.pms.core.model.enums.ReportRowShift;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "report_schedule")
@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ReportScheduleEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column private String indicator;

  @Column
  @Enumerated(EnumType.STRING)
  private ReportRowShift shift;

  // indicates which electrical value position needs to be filled in
  @Column
  @Enumerated(EnumType.STRING)
  private ReportRowPeriod period;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private ReportTypeEntity type;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private SensorConfigurationEntity sensorConfiguration;
}
