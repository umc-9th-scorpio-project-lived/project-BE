package com.lived.domain.routine.controller;

import com.lived.domain.routine.dto.RoutineRecommendResponseDTO;
import com.lived.domain.routine.dto.RoutineResponseDTO;
import com.lived.domain.routine.service.RoutineRecommendationService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Routine Recommendation", description = "추천 관련 API")
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RoutineRecommendationController {

    private final RoutineRecommendationService routineRecommendationService;

    @Operation(
            summary = "[온보딩] 고민 기반 루틴 추천 API",
            description = "사용자가 선택한 고민 ID 리스트를 전달하면 연관된 추천 루틴 목록을 반환합니다."
    )
    @GetMapping("/concerns")
    public ApiResponse<List<RoutineResponseDTO>> getRecommendedRoutines(
            @Parameter(description = "선택된 고민 ID 리스트", example = "1,2,3")
            @RequestParam(name = "concernIds") List<Long> concernIds
    ) {
        List<RoutineResponseDTO> response = routineRecommendationService.getRecommendedRoutines(concernIds);

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, response);
    }

    @Operation(
            summary = "카테고리별 전체 추천 루틴 조회 API",
            description = "카테고리별로 그룹화된 모든 추천 루틴 목록을 반환합니다."
    )
    @GetMapping("/categories")
    public ApiResponse<RoutineRecommendResponseDTO> getRecommendedRoutinesByCategory() {
        RoutineRecommendResponseDTO response = routineRecommendationService.getRecommendedRoutines();
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, response);
    }
}
