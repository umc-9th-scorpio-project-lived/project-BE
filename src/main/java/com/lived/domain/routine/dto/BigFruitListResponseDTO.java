package com.lived.domain.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BigFruitListResponseDTO {
    private boolean hasNext;
    private int currentPage;
    private List<RoutineStatisticsResponseDTO.BigFruitDTO> fruits;
}
