package com.hbc.pms.integration.db.entity;

import com.hbc.pms.core.model.enums.ActionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

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
  private ActionType type;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "alarm_action_recipient",
      joinColumns = @JoinColumn(name = "alarm_action_id"),
      uniqueConstraints = @UniqueConstraint(columnNames = {"alarm_action_id", "recipient"})
  )
  @Column(name = "recipient")
  private Set<String> recipients;
}