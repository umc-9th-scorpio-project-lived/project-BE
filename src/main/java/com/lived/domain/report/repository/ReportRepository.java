package com.lived.domain.report.repository;

import com.lived.domain.report.entity.Report;
import com.lived.domain.report.entity.enums.ReportTargetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

  boolean existsByReporterIdAndTargetTypeAndTargetId(
      Long reporterId,
      ReportTargetType targetType,
      Long targetId
  );
}