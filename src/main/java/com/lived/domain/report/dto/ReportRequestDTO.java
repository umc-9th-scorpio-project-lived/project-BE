package com.lived.domain.report.dto;

import com.lived.domain.report.entity.enums.ReportReason;
import com.lived.domain.report.entity.enums.ReportTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ReportRequestDTO {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "신고 생성 요청")
  public static class CreateReportRequest {

    @Schema(description = "신고 대상 타입", example = "POST", allowableValues = {"POST", "COMMENT"})
    @NotNull(message = "신고 대상 타입은 필수입니다.")
    private ReportTargetType targetType;

    @Schema(description = "신고 대상 ID", example = "123")
    @NotNull(message = "신고 대상 ID는 필수입니다.")
    private Long targetId;

    @Schema(description = "신고 사유", example = "JUNK",
        allowableValues = {"ABUSE", "ADULT", "SPAM", "JUNK", "PRIVACY", "COPYRIGHT", "CRIME", "OTHER"})
    @NotNull(message = "신고 사유는 필수입니다.")
    private ReportReason reason;

    @Schema(description = "상세 사유", example = "도배가 심해요")
    private String detail;
  }
}