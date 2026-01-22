package com.lived.domain.routine.repository;

import com.lived.domain.routine.entity.RoutineHistory;
import com.lived.domain.routine.entity.mapping.MemberRoutine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoutineHistoryRepository extends JpaRepository<RoutineHistory, Long> {
    // 특정 날짜의 수행 기록 조회
    Optional<RoutineHistory> findByMemberRoutineIdAndCheckDate(Long memberRoutineId, LocalDate checkDate);

    // 특정 기간 내 완료된 기록 수
    long countByMemberRoutineIdAndCheckDateBetweenAndIsDoneTrue(
            Long memberRoutineId, LocalDate start, LocalDate end);

    // 특정 루틴 리스트와 날짜에 해당하는 모든 기록을 한 번에 조회
    List<RoutineHistory> findAllByMemberRoutineIdInAndCheckDate(List<Long> memberRoutineIds, LocalDate checkDate);

    // 특정 루틴과 연결된 모든 수행 기록을 삭제
    void deleteAllByMemberRoutineId(Long memberRoutineId);
}
