package com.hbc.pms.integration.db.entity;

import com.hbc.pms.core.model.enums.AlarmStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "alarm_history")
@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AlarmHistoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private AlarmStatusEnum status = AlarmStatusEnum.TRIGGERED;

  @CreationTimestamp
  @Column
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column
  private OffsetDateTime updatedAt;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private AlarmConditionEntity alarmCondition;

}
