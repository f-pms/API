package com.hbc.pms.integration.db.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name = "sensor_configuration_figure")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class SensorConfigurationFigure extends BaseEntity {

  @Column(name = "name")
  private String name;

  @Column(name = "data_type")
  private String dataType;

  @Column(name = "data_block_number")
  private int dataBlockNumber;

  @Column(name = "offset")
  private int offset;

  @ManyToOne
  @JoinColumn(name = "sensor_configuration_id", nullable = false)
  private SensorConfiguration sensorConfiguration;
}
