package com.hbc.pms.integration.db.entity;

import com.hbc.pms.core.model.enums.ReportRowCategory;
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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "report_row")
@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ReportRowEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column private String indicator;

  @Column private Long shift;

  @Column
  @Enumerated(EnumType.STRING)
  private ReportRowCategory category;

  @Column private Double oldElectricValue;

  @Column private Double newElectricValue1;

  @Column private Double newElectricValue2;

  @Column private Double newElectricValue3;

  @Column private Double newElectricValue4;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private ReportEntity report;
}
