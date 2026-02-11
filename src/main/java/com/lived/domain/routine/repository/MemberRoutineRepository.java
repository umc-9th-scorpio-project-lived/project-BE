package com.lived.domain.routine.repository;

import com.lived.domain.routine.entity.mapping.MemberRoutine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRoutineRepository extends JpaRepository<MemberRoutine, Long> {
    // 특정 회원의 활성화된 모든 루틴 조회
    @Query("SELECT DISTINCT mr FROM MemberRoutine mr " +
            "LEFT JOIN FETCH mr.excludedDates " +
            "WHERE mr.member.id = :memberId AND mr.isActive = true")
    List<MemberRoutine> findAllByMemberIdAndIsActiveTrue(@Param("memberId") Long memberId);

    // 특정 회원의 루틴이 이름이 이미 존재하는지 조회
    boolean existsByMemberIdAndTitleAndIsActiveTrue(Long memberId, String title);

    List<MemberRoutine> findAllByMemberId(Long memberId);

    // 수정 시 활용: memberRoutineId를 제외하고, 활성화된 루틴 중 이름 중복 확인
    boolean existsByMemberIdAndTitleAndIsActiveTrueAndIdNot(Long memberId, String title, Long memberRoutineId);
}
