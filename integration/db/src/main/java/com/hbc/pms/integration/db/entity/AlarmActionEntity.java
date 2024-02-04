package com.hbc.pms.integration.db.entity;

import com.hbc.pms.core.model.enums.AlarmActionType;
import jakarta.persistence.*;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "alarm_action")
@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AlarmActionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  @Enumerated(EnumType.STRING)
  private AlarmActionType type;

  @Column private String message;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "alarm_action_recipient",
      joinColumns = @JoinColumn(name = "alarm_action_id"),
      uniqueConstraints = @UniqueConstraint(columnNames = {"alarm_action_id", "recipient"}))
  @Column(name = "recipient")
  private Set<String> recipients;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private AlarmConditionEntity condition;
}
