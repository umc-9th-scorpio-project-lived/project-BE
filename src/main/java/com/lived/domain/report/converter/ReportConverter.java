package com.lived.domain.report.converter;

import com.lived.domain.member.entity.Member;
import com.lived.domain.report.dto.ReportRequestDTO;
import com.lived.domain.report.dto.ReportResponseDTO;
import com.lived.domain.report.entity.Report;

public class ReportConverter {

  // Request → Report Entity
  public static Report toReport(
      ReportRequestDTO.CreateReportRequest request,
      Member reporter
  ) {
    return Report.builder()
        .reporter(reporter)
        .targetType(request.getTargetType())
        .targetId(request.getTargetId())
        .reason(request.getReason())
        .detail(request.getDetail())
        .build();
  }

  // Report Entity → CreateReportResponse
  public static ReportResponseDTO.CreateReportResponse toCreateReportResponse(Report report) {
    return ReportResponseDTO.CreateReportResponse.builder()
        .reportId(report.getId())
        .build();
  }
}