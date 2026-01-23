package com.lived.domain.post.service;

import com.lived.domain.post.converter.CommentConverter;
import com.lived.domain.post.dto.CommentRequestDTO;
import com.lived.domain.post.dto.CommentResponseDTO;
import com.lived.domain.comment.entity.Comment;
import com.lived.domain.post.repository.CommentRepository;
import com.lived.domain.member.entity.Member;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.post.entity.Post;
import com.lived.domain.post.repository.PostRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final MemberRepository memberRepository;

  /**
   * 댓글 작성
   */
  @Transactional
  public CommentResponseDTO.CreateCommentResponse createComment(
      Long postId,
      Long memberId,
      CommentRequestDTO.CreateCommentRequest request
  ) {
    // Post 조회
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.POST_NOT_FOUND));

    // 삭제된 게시글인지 확인
    if (post.getDeletedAt() != null) {
      throw new GeneralException(GeneralErrorCode.POST_NOT_FOUND);
    }

    // Member 조회
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

    // 부모 댓글 조회 (대댓글인 경우)
    Comment parent = null;
    if (request.getParentCommentId() != null) {
      parent = commentRepository.findById(request.getParentCommentId())
          .orElseThrow(() -> new GeneralException(GeneralErrorCode.COMMENT_NOT_FOUND));

      // 부모 댓글이 같은 게시글에 속하는지 확인
      if (!parent.getPost().getId().equals(postId)) {
        throw new GeneralException(GeneralErrorCode.COMMENT_NOT_MATCH_POST);
      }

      // 부모 댓글이 삭제된 댓글인지 확인
      if (parent.getDeletedAt() != null) {
        throw new GeneralException(GeneralErrorCode.COMMENT_NOT_FOUND);
      }
    }

    // Comment 생성
    Comment comment = CommentConverter.toComment(request, post, member, parent);
    Comment savedComment = commentRepository.save(comment);

    // Post의 댓글 수 증가
    post.incrementCommentCount();

    return CommentConverter.toCreateCommentResponse(savedComment);
  }
}