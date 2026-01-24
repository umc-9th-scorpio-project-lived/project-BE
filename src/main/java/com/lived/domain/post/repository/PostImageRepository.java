package com.lived.domain.post.repository;

import com.lived.domain.post.entity.PostImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

  List<PostImage> findAllByPostId(Long postId);

  Optional<PostImage> findFirstByPostIdAndOrderIndex(Long postId, Integer orderIndex);
}