package com.lived.domain.routine.repository;

import com.lived.domain.routine.entity.RoutineHistory;
import com.lived.domain.routine.entity.mapping.MemberRoutine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
    @Modifying //SELECT 없이 바로 DELETE 쿼리
    @Transactional
    @Query("DELETE FROM RoutineHistory rh WHERE rh.memberRoutine.id = :memberRoutineId")
    void deleteAllByMemberRoutineId(@Param("memberRoutineId") Long memberRoutineId);

    // 특정 루틴의 한 달간 모든 기록 조회
    List<RoutineHistory> findAllByMemberRoutineIdAndCheckDateBetween(Long memberRoutineId, LocalDate state, LocalDate end);

    // 완료일 수 계산
    @Query("SELECT COUNT(rh) FROM RoutineHistory rh " +
            "WHERE rh.memberRoutine = :routine " +
            "AND rh.checkDate BETWEEN :startDate AND :endDate " +
            "AND rh.isDone = true")
    long countCompletedDays(MemberRoutine routine, LocalDate startDate, LocalDate endDate);


    @Query("SELECT rh FROM RoutineHistory rh " +
            "JOIN rh.memberRoutine mr " +
            "WHERE mr.member.id = :memberId " +
            "AND rh.checkDate BETWEEN :startDate AND :endDate")
    List<RoutineHistory> findAllByMemberIdAndDateBetween(@Param("memberId") Long memberId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);
}
