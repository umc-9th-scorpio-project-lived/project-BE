package com.lived.domain.routine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "월별 루틴 트래커 전체 조회 응답 (열매 요약 + 루틴별 달력 리스트)")
public record MonthlyTrackerViewResponseDTO(
        @Schema(description = "월간 열매 획득 현황 요약 (골드, 일반, 성장 중 열매 개수)")
        MonthlyFruitSummaryDTO fruitSummaryDTO,

        @Schema(description = "사용자가 등록한 전체 루틴의 월간 상세 트래커 리스트")
        List<RoutineCalenderResponseDTO> routineTrackers,

        @Schema(description = "조회된 연도", example = "2026")
        int year,

        @Schema(description = "조회된 월", example = "1")
        int month
) {}