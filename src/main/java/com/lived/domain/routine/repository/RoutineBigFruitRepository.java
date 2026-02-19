package com.lived.domain.routine.repository;

import com.lived.domain.routine.entity.RoutineBigFruit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutineBigFruitRepository extends JpaRepository<RoutineBigFruit, Long> {
    List<RoutineBigFruit> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);
    Page<RoutineBigFruit> findAllByMemberId(Long memberId, Pageable pageable);
}
