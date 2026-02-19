package com.lived.domain.member.repository;

import com.lived.domain.member.entity.Member;
import com.lived.domain.member.entity.TreeVisibilityTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TreeVisibilityTargetRepository extends JpaRepository<TreeVisibilityTarget, Long> {
    List<TreeVisibilityTarget> findAllByMember(Member member);
    void deleteAllByMember(Member member);
}
