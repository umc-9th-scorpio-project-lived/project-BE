package com.lived.domain.routine.dto;

import com.lived.domain.routine.entity.enums.BigFruitType;
import com.lived.domain.routine.enums.StatisticsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineStatisticsResponseDTO {
    private StatisticsType type; // WEEKELY or MONTHLY
    private String periodTitle; // 예: "2025년 10월", "10월 2주차"
    private String aiAdvice;

    private CompleteRateDTO completionRate;
    private List<DailyStatisticsDTO> dailyGraph;
    private List<BigFruitDTO> bigFruits;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompleteRateDTO {
        private int percentage;
        private int totalCount;
        private int doneCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStatisticsDTO {
        private LocalDate date;
        private String dayOfWeek;
        private int percentage;
        private boolean isDone;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BigFruitDTO {
        private Long id;
        private BigFruitType fruitType;
        private int currentValue;
        private int goalValue;
        private int percentage;
        private String description;
    }
}
