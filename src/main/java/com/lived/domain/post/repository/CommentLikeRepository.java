package com.lived.domain.post.repository;

import com.lived.domain.post.entity.mapping.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

  Optional<CommentLike> findByCommentIdAndMemberId(Long commentId, Long memberId);

  boolean existsByCommentIdAndMemberId(Long commentId, Long memberId);
}