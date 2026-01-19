package com.lived.domain.routine.controller;

import com.lived.domain.routine.dto.RoutineRequestDTO;
import com.lived.domain.routine.service.RoutineService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
