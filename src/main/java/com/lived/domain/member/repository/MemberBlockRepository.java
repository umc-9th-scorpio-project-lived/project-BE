package com.lived.domain.member.repository;

import com.lived.domain.member.entity.Block;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberBlockRepository extends JpaRepository<Block, Long> {

  boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

  Optional<Block> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

  @Query("SELECT b FROM Block b " +
      "JOIN FETCH b.blocked " +
      "WHERE b.blocker.id = :blockerId " +
      "AND b.id < :cursor " +
      "ORDER BY b.id DESC")
  Slice<Block> findBlockListWithCursor(
      @Param("blockerId") Long blockerId,
      @Param("cursor") Long cursor,
      Pageable pageable
  );

  @Query("SELECT b FROM Block b " +
      "JOIN FETCH b.blocked " +
      "WHERE b.blocker.id = :blockerId " +
      "ORDER BY b.id DESC")
  Slice<Block> findBlockListFirstPage(
      @Param("blockerId") Long blockerId,
      Pageable pageable
  );
}