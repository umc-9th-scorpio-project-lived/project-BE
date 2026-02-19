package com.lived.domain.concern.repository;

import com.lived.domain.concern.entity.mapping.ConcernRoutine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcernRoutineRepository extends JpaRepository<ConcernRoutine, Long> {
    List<ConcernRoutine> findAllByConcernIdIn(List<Long> concernIds);
}
