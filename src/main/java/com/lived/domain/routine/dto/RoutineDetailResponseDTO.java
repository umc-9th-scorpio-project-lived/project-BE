package com.lived.domain.routine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

public record RoutineDetailResponseDTO(
        @Schema(description = "멤버 루틴 ID", example = "1")
        Long memberRoutineId,

        @Schema(description = "루틴 제목", example = "일어나자마자 이불 정리하기")
        String title,

        @Schema(description = "루틴 이모지", example = "🛏️")
        String emoji,

        @Schema(description = "반복 유형", example = "WEEKLY")
        String repeatType,

        @Schema(description = "반복 간격", example = "1")
        Integer repeatInterval,

        @Schema(description = "반복 값 (요일 등)", example = "MONDAY,WEDNESDAY")
        String repeatValue,

        @Schema(description = "알림 시간", example = "12:00:00")
        LocalTime alarmTime,

        @Schema(description = "알림 설정 여부", example = "true")
        boolean isAlarmOn
) {
}
