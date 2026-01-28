package com.lived.domain.routine.service;

import com.lived.domain.concern.entity.mapping.ConcernRoutine;
import com.lived.domain.concern.repository.ConcernRoutineRepository;
import com.lived.domain.routine.converter.RoutineConverter;
import com.lived.domain.routine.dto.RoutineRecommendResponseDTO;
import com.lived.domain.routine.dto.RoutineResponseDTO;
import com.lived.domain.routine.entity.Routine;
import com.lived.domain.routine.entity.RoutineCategory;
import com.lived.domain.routine.entity.enums.CategoryName;
import com.lived.domain.routine.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineRecommendationService {

    private final ConcernRoutineRepository concernRoutineRepository;
    private final RoutineRepository routineRepository;

    // [온보딩] 고민 키워드 기반 루틴 추천
    public List<RoutineResponseDTO> getRecommendedRoutines(List<Long> concernIds) {
        List<ConcernRoutine> mappings = concernRoutineRepository.findAllByConcernIdIn(concernIds);

        List<Routine> routines = mappings.stream()
                .map(ConcernRoutine::getRoutine)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(routines);

        return routines.stream()
                .map(RoutineConverter::toRoutineResponseDTO)
                .toList();
    }

    // DB에 있는 추천 루틴 조회
    public RoutineRecommendResponseDTO getRecommendedRoutines() {
        List<Routine> allTemplates = routineRepository.findAllWithCategory();

        Map<CategoryName, List<Routine>> groupedByCategory = allTemplates.stream()
                .collect(Collectors.groupingBy(r -> r.getCategory().getName()));

        List<RoutineRecommendResponseDTO.CategorySectionDTO> categorySections = groupedByCategory.entrySet().
                stream().sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    CategoryName categoryName = entry.getKey();
                    List<Routine> routinesInCategory = entry.getValue();

                    RoutineCategory categoryEntity = routinesInCategory.getFirst().getCategory();

                    List<RoutineRecommendResponseDTO.RecommendItemDTO> items = routinesInCategory.stream()
                            .map(r -> RoutineRecommendResponseDTO.RecommendItemDTO.builder()
                                    .routineId(r.getId())
                                    .title(r.getTitle())
                                    .emoji(categoryEntity.getEmoji())
                                    .build())
                            .toList();

                    return RoutineRecommendResponseDTO.CategorySectionDTO.builder()
                            .categoryName(getKoreanCategoryName(categoryName))
                            .categoryEmoji(categoryEntity.getEmoji())
                            .routines(items)
                            .build();
                })
                .toList();

        return RoutineRecommendResponseDTO.builder()
                .categories(categorySections)
                .build();

    }

    private String getKoreanCategoryName(CategoryName name) {
        return switch (name) {
            case LIFESTYLE -> "생활 습관";
            case CLEANING -> "청소";
            case HEALTH -> "건강";
            case EATING_HABIT -> "식습관";
            case MINDFULNESS -> "마음 챙기기";
        };
    }
}
