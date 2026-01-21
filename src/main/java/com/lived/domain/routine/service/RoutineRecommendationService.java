package com.lived.domain.routine.service;

import com.lived.domain.concern.entity.mapping.ConcernRoutine;
import com.lived.domain.concern.repository.ConcernRoutineRepository;
import com.lived.domain.routine.converter.RoutineConverter;
import com.lived.domain.routine.dto.RoutineResponseDTO;
import com.lived.domain.routine.entity.Routine;
import com.lived.domain.routine.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
}
