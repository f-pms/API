package com.hbc.pms.integration.db.entity;

import com.hbc.pms.core.model.enums.AlarmStatus;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
  private AlarmStatus status = AlarmStatus.TRIGGERED;

  @CreationTimestamp
  @Column(updatable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column
  private OffsetDateTime updatedAt;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private AlarmConditionEntity alarmCondition;

}
