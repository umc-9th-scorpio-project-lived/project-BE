package com.lived.domain.routine.repository;

import com.lived.domain.routine.entity.mapping.MemberRoutine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRoutineRepository extends JpaRepository<MemberRoutine, Long> {
    // 특정 회원의 활성화된 모든 루틴 조회
    List<MemberRoutine> findAllByMemberIdAndIsActiveTrue(Long memberId);
}
