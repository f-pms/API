package com.hbc.pms.integration.db.entity;

import com.hbc.pms.core.model.enums.AlarmActionType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import jakarta.persistence.UniqueConstraint;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
