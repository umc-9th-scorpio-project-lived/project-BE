package com.lived.domain.routine.repository;

import com.lived.domain.routine.entity.RoutineFruit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface RoutineFruitRepository extends JpaRepository<RoutineFruit, Long> {
    // 특정 루틴의 열매 정보 조회
    Optional<RoutineFruit> findByMemberRoutineIdAndMonth(Long memberRoutineId, LocalDate month);
}
