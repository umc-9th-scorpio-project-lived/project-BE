package com.lived.domain.routine.controller;

import com.lived.domain.routine.dto.*;
import com.lived.domain.routine.enums.StatisticsType;
import com.lived.domain.routine.service.RoutineStatisticsService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import com.lived.global.auth.annotation.AuthMember;
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

    @Operation(summary = "루틴 나무 전체 데이터 조회", description = "월별 열매 통계 요약과 나무에 표시될 열매 리스트를 한번에 조회합니다.")
    @GetMapping("/tree")
    public ApiResponse<RoutineTreeResponseDTO> getRoutineTree(
            @AuthMember Long memberId,
            @RequestParam(name = "year") int year,
            @RequestParam(name = "month") int month
    ) {
        RoutineTreeResponseDTO result = routineStatisticsService.getRoutineTree(memberId, year, month);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @Operation(
            summary = "월별 루틴 트래커 전체 데이터 조회",
            description = "특정 멤버의 해당 월 열매 요약과 모든 루틴별 달성 상태 리스트를 한 번에 조회합니다.")
    @GetMapping("/calender")
    public ApiResponse<MonthlyTrackerViewResponseDTO> getMonthlyCalender(
            @AuthMember Long memberId,
            @Parameter(description = "조회할 연도 (YYYY)", example = "2024") @RequestParam int year,
            @Parameter(description = "조회할 월 (1-12)", example = "12") @RequestParam int month) {

         MonthlyTrackerViewResponseDTO result = routineStatisticsService.getMonthlyTrackerView(memberId, year, month);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @Operation(
            summary = "월별 전체 열매 개수 요약 조회",
            description = "특정 멤버의 월별 열매(골드, 일반, 성장 중) 통계를 집계하여 반환합니다.")
    @GetMapping("/fruits/summary")
    public ApiResponse<MonthlyFruitSummaryDTO> getMonthlyFruitSummary(
            @AuthMember Long memberId,
            @Parameter(description = "조회할 연도 (YYYY)", example = "2024") @RequestParam int year,
            @Parameter(description = "조회할 월 (1-12)", example = "12") @RequestParam int month) {

        MonthlyFruitSummaryDTO result = routineStatisticsService.getMonthlyFruitSummary(memberId, year, month);

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @Operation(
            summary = "열매 팝업 데이터 조회",
            description = "나무의 열매를 클릭했을 때 나타나는 루틴 이름과 달성률 요약을 조회합니다. (열매 팝업용)")
    @GetMapping("/routines/{memberRoutineId}/popup")
    public ApiResponse<FruitPopupResponseDTO> getFruitPopup(
            @Parameter(description = "멤버 루틴 ID", example = "101") @PathVariable Long memberRoutineId,
            @Parameter(description = "조회할 연도 (YYYY)", example = "2026") @RequestParam int year,
            @Parameter(description = "조회할 월 (1-12)", example = "1") @RequestParam int month) {

        FruitPopupResponseDTO result = routineStatisticsService.getRoutinePopup(memberRoutineId, year, month);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @Operation(
            summary = "루틴 나무 모아보기",
            description = "월별 나무 현황을 최신순으로 페이징하여 조회합니다. (page는 0부터 시작, size는 한 번에 불러올 월의 개수)"
    )
    @GetMapping("/trees")
    public ApiResponse<RoutineTreeListResponseDTO> getRoutineTreeList(
            @AuthMember Long memberId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "한 번에 불러올 개수", example = "5")
            @RequestParam(name = "size", defaultValue = "5") int size) {
        RoutineTreeListResponseDTO result = routineStatisticsService.getRoutineTreeListPaging(memberId, page, size);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @Operation(
            summary = "나의 주간/월간 통계 조회",
            description = "주간 또는 월간 루틴 달성률, AI 조언, 그래프 데이터를 조회합니다."
    )
    @GetMapping("/me")
    public ApiResponse<RoutineStatisticsResponseDTO> getMyStatistics(
            @AuthMember Long memberId,
            @Parameter(description = "조회할 연도", example = "2025") @RequestParam int year,
            @Parameter(description = "조회할 월", example = "10") @RequestParam int month,
            @Parameter(description = "주차 (주간 통계일 경우 필수)", example = "2") @RequestParam(required = false) Integer week,
            @Parameter(description = "통계 타입 (WEEKLY, MONTHLY)", example = "WEEKLY") @RequestParam StatisticsType type) {
        RoutineStatisticsResponseDTO result = routineStatisticsService.getMyStatistics(memberId, year, month, week, type);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }
}
