package com.lived.domain.routine.dto;

import java.util.List;

public record RoutineAiBatchSaveRequestDTO(
        List<RoutineAiSaveItemDTO> routines
) {
    public record RoutineAiSaveItemDTO(
            String title,
            String emoji
    ){}
}