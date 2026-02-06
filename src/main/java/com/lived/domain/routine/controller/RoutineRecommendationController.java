package com.lived.domain.routine.controller;

import com.lived.domain.routine.dto.RoutineAiRecommendResponseDTO;
import com.lived.domain.routine.dto.RoutineBatchAddRequestDTO;
import com.lived.domain.routine.dto.RoutineRecommendResponseDTO;
import com.lived.domain.routine.dto.RoutineResponseDTO;
import com.lived.domain.routine.service.RoutineRecommendationService;
import com.lived.domain.routine.service.RoutineService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import com.lived.global.auth.annotation.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Routine Recommendation", description = "추천 관련 API")
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RoutineRecommendationController {

    private final RoutineRecommendationService routineRecommendationService;
    private final RoutineService routineService;

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

    @Operation(
            summary = "추천 루틴 일괄 등록 API",
            description = "선택된 루틴 템플릿들을 내 루틴으로 한 번에 등록합니다. 온보딩 및 추천 기능에서 공통으로 사용됩니다."
    )
    @PostMapping("/batch")
    public ApiResponse<String> addRoutinesBatch(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @AuthMember Long memberid,
            @RequestBody RoutineBatchAddRequestDTO request
    ) {
        int addedCount = routineService.registerRoutinesBatch(memberid, request);

        if(addedCount == 0) {
            return ApiResponse.onSuccess(GeneralSuccessCode.OK,"이미 모든 루틴이 등록되어 있습니다.");
        }

        return ApiResponse.onSuccess(GeneralSuccessCode.ROUTINE_CREATED,
                String.format("선택하신 %d개의 루틴이 추가되었습니다.", addedCount));
    }

    @Operation(
            summary = "AI 루틴 추천 리스트 조회 API",
            description = "사용자의 현재 루틴을 분석하여 시너지가 날 만한 새로운 루틴들을 Gemini AI가 추천해줍니다."
    )
    @GetMapping("/ai")
    public ApiResponse<List<RoutineAiRecommendResponseDTO>> getAiRecommendations(Long memberId) {

        List<RoutineAiRecommendResponseDTO> response =
                routineRecommendationService.getAiRecommendations(memberId);

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, response);
    }
}
