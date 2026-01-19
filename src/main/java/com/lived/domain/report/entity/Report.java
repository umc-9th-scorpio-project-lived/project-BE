package com.lived.domain.report.entity;

import com.lived.domain.member.entity.Member;
import com.lived.domain.report.entity.enums.ReportReason;
import com.lived.domain.report.entity.enums.ReportTargetType;
import com.lived.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Report extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "report_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "reporter_id", nullable = false)
  private Member reporter;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_type", nullable = false)
  private ReportTargetType targetType;

  @Column(name = "target_id", nullable = false)
  private Long targetId;

  @Enumerated(EnumType.STRING)
  @Column(name = "reason", nullable = false)
  private ReportReason reason;

  @Column(name = "detail", columnDefinition = "TEXT")
  private String detail;
}