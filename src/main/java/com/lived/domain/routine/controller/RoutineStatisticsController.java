package com.lived.domain.routine.controller;

import com.lived.domain.routine.dto.MonthlyFruitSummaryDTO;
import com.lived.domain.routine.dto.MonthlyTrackerViewResponseDTO;
import com.lived.domain.routine.service.RoutineStatisticsService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "Routine Statistics", description = "루틴 통계 및 트래커 관련 API")
public class RoutineStatisticsController {

    private final RoutineStatisticsService routineStatisticsService;

    @Operation(
            summary = "월별 루틴 트래커 전체 데이터 조회",
            description = "특정 멤버의 해당 월 열매 요약과 모든 루틴별 달성 상태 리스트를 한 번에 조회합니다.")
    @GetMapping("routines/{memberId}/calender")
    public ApiResponse<MonthlyTrackerViewResponseDTO> getMonthlyCalender(
            @Parameter(description = "멤버 ID", example = "1") @PathVariable Long memberId,
            @Parameter(description = "조회할 연도 (YYYY)", example = "2024") @RequestParam int year,
            @Parameter(description = "조회할 월 (1-12)", example = "12") @RequestParam int month) {

         MonthlyTrackerViewResponseDTO result = routineStatisticsService.getMonthlyTrackerView(memberId, year, month);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @Operation(
            summary = "월별 전체 열매 개수 요약 조회",
            description = "특정 멤버의 월별 열매(골드, 일반, 성장 중) 통계를 집계하여 반환합니다.")
    @GetMapping("/members/{memberId}/fruits/summary")
    public ApiResponse<MonthlyFruitSummaryDTO> getMonthlyFruitSummary(
            @Parameter(description = "멤버 고유 ID", example = "1") @PathVariable Long memberId,
            @Parameter(description = "조회할 연도 (YYYY)", example = "2024") @RequestParam int year,
            @Parameter(description = "조회할 월 (1-12)", example = "12") @RequestParam int month) {

        MonthlyFruitSummaryDTO result = routineStatisticsService.getMonthlyFruitSummary(memberId, year, month);

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }



}
