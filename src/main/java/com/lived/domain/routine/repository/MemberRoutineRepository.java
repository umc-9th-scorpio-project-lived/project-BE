package com.lived.domain.routine.repository;

import com.lived.domain.routine.entity.mapping.MemberRoutine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRoutineRepository extends JpaRepository<MemberRoutine, Long> {
    // 특정 회원의 활성화된 모든 루틴 조회
    List<MemberRoutine> findAllByMemberIdAndIsActiveTrue(Long memberId);

    // 특정 회원의 루틴이 이름이 이미 존재하는지 조회
    boolean existsByMemberIdAndTitleAndIsActiveTrue(Long memberId, String title);
}
