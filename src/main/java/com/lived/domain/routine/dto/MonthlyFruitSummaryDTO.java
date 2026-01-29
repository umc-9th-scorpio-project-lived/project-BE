package com.lived.domain.routine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "월별 열매 통계 요약 정보")
public record MonthlyFruitSummaryDTO(
        @Schema(description = "골드 등급 열매 수", example = "3")
        long goldCount,

        @Schema(description = "일반 등급 과일 수", example = "5")
        long normalCount,

        @Schema(description = "현재 재배 중인 과일 수", example = "1")
        long growingCount
) {}
