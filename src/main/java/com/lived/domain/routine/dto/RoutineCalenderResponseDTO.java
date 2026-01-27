package com.lived.domain.routine.dto;

import com.lived.domain.routine.entity.enums.DayStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "루틴 월별 트래커 응답 정보")
public record RoutineCalenderResponseDTO(
        @Schema(description = "멤버 루틴 PK", example = "1")
        Long memberRoutineId,

        @Schema(description = "루틴 제목", example = "매일 물 2L 마시기")
        String title,

        @Schema(description = "연도", example = "2024")
        int year,

        @Schema(description = "월", example = "12")
        int month,

        @Schema(description = "해당 월의 날짜별 상태 리스트")
        List<DayResponseDTO> days
) {
    @Schema(description = "날짜별 루틴 상태 정보")
    public record DayResponseDTO(
            @Schema(description = "일(Day)", example = "25")
            int day,

            @Schema(description = "해당 날짜의 루틴 상태 (예: SUCCESS, FAIL, HOLD)", example = "SUCCESS")
            DayStatus status
    ) {}
}
