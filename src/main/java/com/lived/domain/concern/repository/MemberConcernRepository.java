package com.lived.domain.concern.repository;

import com.lived.domain.concern.entity.mapping.MemberConcern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberConcernRepository extends JpaRepository<MemberConcern, Long> {
    List<MemberConcern> findAllByMemberId(Long memberId);
}
