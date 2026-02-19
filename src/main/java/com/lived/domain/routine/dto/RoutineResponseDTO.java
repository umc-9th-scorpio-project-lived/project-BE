package com.lived.domain.routine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "추천 루틴 응답 DTO")
public record RoutineResponseDTO(
        @Schema(description = "루틴 ID", example = "1")
        Long routineId,

        @Schema(description = "루틴 제목", example = "3분 아침 스트레칭")
        String title,

        @Schema(description = "루틴 이모지", example = "👍")
        String emoji
) {
}
