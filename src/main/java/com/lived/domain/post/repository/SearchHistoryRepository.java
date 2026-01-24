package com.lived.domain.post.repository;

import com.lived.domain.post.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

  List<SearchHistory> findByMemberIdOrderBySearchedAtDesc(Long memberId);

  Optional<SearchHistory> findByMemberIdAndKeyword(Long memberId, String keyword);

  long countByMemberId(Long memberId);

  void deleteByMemberId(Long memberId);
}