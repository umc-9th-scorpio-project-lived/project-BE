package com.lived.domain.routine.controller;

import com.lived.domain.routine.dto.HomeRoutineResponseDTO;
import com.lived.domain.routine.dto.RoutineRequestDTO;
import com.lived.domain.routine.service.RoutineService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

}
