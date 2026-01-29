package com.lived.domain.member.repository;

import com.lived.domain.member.entity.Member;
import com.lived.domain.member.entity.RoutineVisibilityTarget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineVisibilityTargetRepository extends JpaRepository<RoutineVisibilityTarget, Long> {
    void deleteAllByMember(Member member);
}
