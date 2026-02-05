package com.lived.domain.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder // [해결 2] @Builder 어노테이션 추가
@NoArgsConstructor
@AllArgsConstructor
public class BigFruitListResponseDTO {
    private List<RoutineStatisticsResponseDTO.BigFruitDTO> fruits;
}
