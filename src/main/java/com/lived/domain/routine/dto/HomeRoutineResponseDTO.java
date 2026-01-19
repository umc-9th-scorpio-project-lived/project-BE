package com.lived.domain.routine.dto;


import java.time.LocalTime;
import java.util.List;

public record HomeRoutineResponseDTO(
        String dateTitle,
        String fullDate,
        String progressMessage,
        List<RoutineItem> routines
) {
    /**
     * 루틴 목록의 개별 항목
     */
    public record RoutineItem(
            Long memberRoutineId,
            String title,
            String emoji,
            LocalTime alarmTime,
            boolean isDone
    ){
    }
}
