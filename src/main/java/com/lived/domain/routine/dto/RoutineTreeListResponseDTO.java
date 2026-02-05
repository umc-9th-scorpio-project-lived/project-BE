package com.lived.domain.routine.dto;

import com.lived.domain.routine.entity.enums.FruitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineTreeListResponseDTO {

    private boolean hasNext;
    private int currentPage;
    private List<MonthlyTreeInfoDTO> trees;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyTreeInfoDTO {
        private int year;
        private int month;

        private List<FruitPreviewDTO> fruits;

        private int goldCount;
        private int normalCount;
        private int growingCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FruitPreviewDTO {
        private int memberRoutineId;
        private FruitType fruitType;
    }
}
