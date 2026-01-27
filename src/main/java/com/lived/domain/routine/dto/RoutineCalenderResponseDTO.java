package com.lived.domain.routine.dto;

import com.lived.domain.routine.entity.enums.DayStatus;

import java.util.List;

public record RoutineCalenderResponseDTO(
        Long memberRoutineId,
        String title,
        int year,
        int month,
        List<DayResponseDTO> days
) {
    public record DayResponseDTO(
            int day,
            DayStatus status
    ) {}
}
