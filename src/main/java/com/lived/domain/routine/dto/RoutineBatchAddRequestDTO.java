package com.lived.domain.routine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "추천 루틴 일관 추가 요청 DTO")
public record RoutineBatchAddRequestDTO(
        @Schema(description = "선택된 추천 루틴 ID 리스트" , example = "[1,5,12]")
        List<Long> routindIds
) {}
