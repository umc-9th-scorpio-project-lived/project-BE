package com.lived.domain.post.repository;

import com.lived.domain.post.entity.mapping.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

  Optional<PostLike> findByPostIdAndMemberId(Long postId, Long memberId);

  boolean existsByPostIdAndMemberId(Long postId, Long memberId);

  int countByPostId(Long postId);
}