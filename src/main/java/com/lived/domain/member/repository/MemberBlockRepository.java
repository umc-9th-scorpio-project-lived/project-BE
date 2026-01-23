package com.lived.domain.member.repository;

import com.lived.domain.member.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberBlockRepository extends JpaRepository<Block, Long> {

  boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

}