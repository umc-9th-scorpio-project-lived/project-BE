package com.lived.domain.routine.controller;

import com.lived.domain.routine.dto.*;
import com.lived.domain.routine.service.RoutineService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Routine", description = "루틴 관련 API")
@RestController
@RequestMapping("/api/routines")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineService routineService;

    @Operation(summary = "커스텀 루틴 추가 API", description = "새로운 루틴을 생성합니다. 성공 시 생성된 루틴의 ID를 반환")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "루틴 생성 성공입니다. (ROUTINE201_1)")
    })
    @PostMapping
    public ApiResponse<Long> addCustomRoutine(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestHeader("Member-Id") Long memberId,
            @RequestBody RoutineRequestDTO requestDTO) {

        Long routineId = routineService.createCustomRoutine(memberId, requestDTO);

        return ApiResponse.onSuccess(GeneralSuccessCode.ROUTINE_CREATED, routineId);
    }

    @Operation(
            summary = "홈 화면 루틴 조회 API",
            description = "특정 날짜의 루틴 목록, 달성 메시지, 날짜 정보를 조회합니다."
    )
    @GetMapping("/home")
    public ApiResponse<HomeRoutineResponseDTO> getHomeRoutines(
            @RequestParam(name = "memberId") Long memberId,
            @Parameter(description = "조회할 날짜(YYYY-MM-DD)", example = "2026-01-19")
            @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date
            ) {
        HomeRoutineResponseDTO response = routineService.getHomeRoutines(memberId, date);
        return ApiResponse.onSuccess(GeneralSuccessCode.ROUTINE_OK, response);
    }

    @Operation(
            summary = "루틴 수정 API",
            description = "기존 루틴의 정보를 수정합니다. 수정은 과거와 미래의 모든 일정에 반영됩니다."
    )
    @PatchMapping("/{memberRoutineId}")
    public ApiResponse<String> updateRoutine(
            @Parameter(description = "수정할 루틴의 ID", example = "1")
            @PathVariable Long memberRoutineId,
            @Valid @RequestBody RoutineUpdateRequestDTO request
            ) {
        routineService.updateRoutine(memberRoutineId, request);
        return ApiResponse.onSuccess(GeneralSuccessCode.ROUTINE_UPDATED, "루틴 수정이 완료되었습니다.");
    }

    @Operation(
            summary = "루틴 삭제 API",
            description = "삭제 타입(ONLY_SET(이 일정에만), AFTER_SET(이후 일정에도), ALL_SET(모든 일정에))에 따라 루틴을 삭제"
    )
    @DeleteMapping("/{memberRoutineId}")
    public ApiResponse<String> deleteRoutine(
            @Parameter(description = "삭제할 memberRoutineId", example = "1")
            @PathVariable Long memberRoutineId,
            @Valid @RequestBody RoutineDeleteRequestDTO request
    ) {
        routineService.deleteRoutine(memberRoutineId, request);
        return ApiResponse.onSuccess(GeneralSuccessCode.ROUTINE_DELETED, "루틴 삭제가 완료되었습니다.");
    }

    @Operation(
            summary = "루틴 완료 체크/해제 API",
            description = "루인틔 완료 상태를 토글합니다. 기록이 없으면 생성(true), 있으면 상태를 반전시킵니다."
    )
    @PatchMapping("/{memberRoutineId}/check")
    public ApiResponse<Boolean> toggleRoutineCheck(
            @PathVariable Long memberRoutineId,
            @Parameter(description = "체크할 날짜", example = "2026-01-21")
            @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        boolean isDone = routineService.toggleRoutineCheck(memberRoutineId, date);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, isDone);
    }

    @Operation(
            summary = "추천 루틴 일괄 등록 API",
            description = "선택된 루틴 템플릿들을 내 루틴으로 한 번에 등록합니다. 온보딩 및 추천 기능에서 공통으로 사용됩니다."
    )
    @PostMapping("/batch")
    public ApiResponse<String> addRoutinesBatch(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestHeader("memberId") Long memberid,
            @RequestBody RoutineBatchAddRequestDTO request
    ) {
        int addedCount = routineService.registerRoutinesBatch(memberid, request);

        if(addedCount == 0) {
            return ApiResponse.onSuccess(GeneralSuccessCode.OK,"이미 모든 루틴이 등록되어 있습니다.");
        }

        return ApiResponse.onSuccess(GeneralSuccessCode.ROUTINE_CREATED,
                String.format("선택하신 %d개의 루틴이 추가되었습니다.", addedCount));
    }


}
