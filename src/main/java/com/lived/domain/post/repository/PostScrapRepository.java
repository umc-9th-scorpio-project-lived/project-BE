package com.lived.domain.post.repository;

import com.lived.domain.post.entity.mapping.PostScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {

  Optional<PostScrap> findByPostIdAndMemberId(Long postId, Long memberId);

  boolean existsByPostIdAndMemberId(Long postId, Long memberId);
}