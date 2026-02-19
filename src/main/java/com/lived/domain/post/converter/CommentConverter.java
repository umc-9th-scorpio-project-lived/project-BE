package com.lived.domain.post.converter;

import com.lived.domain.post.dto.CommentRequestDTO;
import com.lived.domain.post.dto.CommentResponseDTO;
import com.lived.domain.comment.entity.Comment;
import com.lived.domain.member.entity.Member;
import com.lived.domain.post.entity.Post;

public class CommentConverter {

  // Request → Comment Entity
  public static Comment toComment(
      CommentRequestDTO.CreateCommentRequest request,
      Post post,
      Member member,
      Comment parent
  ) {
    return Comment.builder()
        .post(post)
        .member(member)
        .parent(parent)
        .content(request.getContent())
        .likeCount(0)
        .build();
  }

  // Comment Entity → CreateCommentResponse
  public static CommentResponseDTO.CreateCommentResponse toCreateCommentResponse(Comment comment) {
    return CommentResponseDTO.CreateCommentResponse.builder()
        .commentId(comment.getId())
        .createdAt(comment.getCreatedAt())
        .build();
  }

  // Comment Entity → UpdateCommentResponse
  public static CommentResponseDTO.UpdateCommentResponse toUpdateCommentResponse(Comment comment) {
    return CommentResponseDTO.UpdateCommentResponse.builder()
        .commentId(comment.getId())
        .build();
  }

  // Comment Entity → DeleteCommentResponse
  public static CommentResponseDTO.DeleteCommentResponse toDeleteCommentResponse(Comment comment) {
    return CommentResponseDTO.DeleteCommentResponse.builder()
        .commentId(comment.getId())
        .build();
  }

  // 좋아요 토글 결과 → ToggleLikeResponse
  public static CommentResponseDTO.ToggleLikeResponse toToggleLikeResponse(
      boolean isLiked,
      int likeCount
  ) {
    return CommentResponseDTO.ToggleLikeResponse.builder()
        .isLiked(isLiked)
        .likeCount(likeCount)
        .build();
  }
}