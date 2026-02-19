package com.lived.domain.routine.converter;

import com.lived.domain.routine.dto.RoutineTreeListResponseDTO;
import com.lived.domain.routine.entity.RoutineFruit;
import com.lived.domain.routine.enums.FruitType;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RoutineStatisticsConverter {

    public RoutineTreeListResponseDTO toRoutineTreePagingResponseDTO(
            List<RoutineFruit> fetchedFruits,
            int size,
            int page,
            boolean hasNext
    ) {
        Map<YearMonth, List<RoutineFruit>> fruitsMap = fetchedFruits.stream()
                .collect(Collectors.groupingBy(f -> YearMonth.from(f.getMonth())));

        List<RoutineTreeListResponseDTO.MonthlyTreeInfoDTO> treeInfos = new ArrayList<>();

        YearMonth currentBase = YearMonth.now();

        YearMonth startMonthOfPage = currentBase.minusMonths((long) page * size);

        for (int i = 0; i < size; i++) {
            YearMonth targetYm = startMonthOfPage.minusMonths(i);

            List<RoutineFruit> monthlyFruits = fruitsMap.getOrDefault(targetYm, new ArrayList<>());
            int gold = (int) monthlyFruits.stream().filter(f -> f.getFruitType() == FruitType.GOLD).count();
            int normal = (int) monthlyFruits.stream().filter(f -> f.getFruitType() == FruitType.NORMAL).count();
            int growing = (int) monthlyFruits.stream().filter(f -> f.getFruitType() == FruitType.GROWING).count();

            List<RoutineTreeListResponseDTO.FruitPreviewDTO> fruitPreviews = monthlyFruits.stream()
                    .map(f -> RoutineTreeListResponseDTO.FruitPreviewDTO.builder()
                            .memberRoutineId(f.getMemberRoutine().getId().intValue()) // Long -> int 형변환
                            .fruitType(f.getFruitType())
                            .build())
                    .toList();

            treeInfos.add(RoutineTreeListResponseDTO.MonthlyTreeInfoDTO.builder()
                    .year(targetYm.getYear())
                    .month(targetYm.getMonthValue())
                    .fruits(fruitPreviews)
                    .goldCount(gold)
                    .normalCount(normal)
                    .growingCount(growing)
                    .build());
        }

        return RoutineTreeListResponseDTO.builder()
                .hasNext(hasNext)
                .currentPage(page)
                .trees(treeInfos)
                .build();
    }
}