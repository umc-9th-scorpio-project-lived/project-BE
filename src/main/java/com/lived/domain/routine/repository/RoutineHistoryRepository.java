package com.lived.domain.routine.repository;

import com.lived.domain.routine.entity.RoutineHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface RoutineHistoryRepository extends JpaRepository<RoutineHistory, Long> {
    // 특정 날짜의 수행 기록 조회
    Optional<RoutineHistory> findByMemberRoutineIdAndCheckDate(Long memberRoutineId, LocalDate checkDate);

    // 특정 기간 내 완료된 기록 수
    long countByMemberRoutineIdAndCheckDateBetweenAndIsDoneTrue(
            Long memberRoutineId, LocalDate start, LocalDate end);
}
