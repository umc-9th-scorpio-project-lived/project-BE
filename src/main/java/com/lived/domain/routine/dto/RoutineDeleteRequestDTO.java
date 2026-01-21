package com.lived.domain.routine.dto;

import com.lived.domain.routine.entity.enums.DeleteType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "루틴 삭제 요청 DTO")
public record RoutineDeleteRequestDTO(

        @Schema(description = "삭제 타입", example = "ONLY_SET", allowableValues = {"ONLY_SET(이 일정에만 적용)", "AFTER_SET(이후 일정에도 적용)", "ALL_SET(모든 일정에 적용)"})
        DeleteType deleteType,

        @Schema(description = "삭제 대상 날짜", example = "2026-01-20")
        LocalDate targetDate
) {}
