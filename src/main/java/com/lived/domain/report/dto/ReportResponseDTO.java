package com.lived.domain.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReportResponseDTO {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "신고 생성 응답")
  public static class CreateReportResponse {

    @Schema(description = "생성된 신고 ID", example = "9001")
    private Long reportId;
  }
}