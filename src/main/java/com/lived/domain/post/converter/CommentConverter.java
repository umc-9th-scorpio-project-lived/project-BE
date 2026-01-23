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
}