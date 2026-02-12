package com.lived.domain.post.service;

import com.lived.domain.member.repository.MemberBlockRepository;
import com.lived.domain.notification.dto.NotificationEvent;
import com.lived.domain.notification.enums.TargetType;
import com.lived.domain.post.converter.CommentConverter;
import com.lived.domain.post.dto.CommentRequestDTO;
import com.lived.domain.post.dto.CommentResponseDTO;
import com.lived.domain.comment.entity.Comment;
import com.lived.domain.post.entity.mapping.CommentLike;
import com.lived.domain.post.repository.CommentLikeRepository;
import com.lived.domain.post.repository.CommentRepository;
import com.lived.domain.member.entity.Member;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.post.entity.Post;
import com.lived.domain.post.repository.PostRepository;
import com.lived.domain.report.repository.ReportRepository;
import com.lived.domain.report.entity.enums.ReportTargetType;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
  private final CommentLikeRepository commentLikeRepository;
  private final MemberBlockRepository memberBlockRepository;
  private final ReportRepository reportRepository;
  private final ApplicationEventPublisher eventPublisher;


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

    // 본인의 글에 본인이 댓글을 단 경우가 아닐 때, 알림 이벤트 발행
    if (!post.getMember().getId().equals(memberId)) {
      eventPublisher.publishEvent(NotificationEvent.builder()
              .receiver(post.getMember())
              .title("새 댓글 알림")
              .content(member.getNickname() + "님이 댓글을 남겼습니다: " + savedComment.getContent())
              .targetId(post.getId())
              .targetType(TargetType.COMMENT)
              .build());
    }

    return CommentConverter.toCreateCommentResponse(savedComment);
  }

  /**
   * 댓글 수정
   */
  @Transactional
  public CommentResponseDTO.UpdateCommentResponse updateComment(
      Long postId,
      Long commentId,
      Long memberId,
      CommentRequestDTO.UpdateCommentRequest request
  ) {
    // Post 조회
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.POST_NOT_FOUND));

    // 삭제된 게시글인지 확인
    if (post.getDeletedAt() != null) {
      throw new GeneralException(GeneralErrorCode.POST_NOT_FOUND);
    }

    // Comment 조회
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.COMMENT_NOT_FOUND));

    // 삭제된 댓글인지 확인
    if (comment.getDeletedAt() != null) {
      throw new GeneralException(GeneralErrorCode.COMMENT_NOT_FOUND);
    }

    // 댓글이 해당 게시글에 속하는지 확인
    if (!comment.getPost().getId().equals(postId)) {
      throw new GeneralException(GeneralErrorCode.COMMENT_NOT_MATCH_POST);
    }

    // 권한 확인 (작성자만 수정 가능)
    if (!comment.getMember().getId().equals(memberId)) {
      throw new GeneralException(GeneralErrorCode.COMMENT_FORBIDDEN);
    }

    // 댓글 수정
    comment.updateContent(request.getContent());

    return CommentConverter.toUpdateCommentResponse(comment);
  }

  /**
   * 댓글 삭제
   */
  @Transactional
  public CommentResponseDTO.DeleteCommentResponse deleteComment(
      Long postId,
      Long commentId,
      Long memberId
  ) {
    // Post 조회
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.POST_NOT_FOUND));

    // 삭제된 게시글인지 확인
    if (post.getDeletedAt() != null) {
      throw new GeneralException(GeneralErrorCode.POST_NOT_FOUND);
    }

    // Comment 조회
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.COMMENT_NOT_FOUND));

    // 이미 삭제된 댓글인지 확인
    if (comment.getDeletedAt() != null) {
      throw new GeneralException(GeneralErrorCode.COMMENT_NOT_FOUND);
    }

    // 댓글이 해당 게시글에 속하는지 확인
    if (!comment.getPost().getId().equals(postId)) {
      throw new GeneralException(GeneralErrorCode.COMMENT_NOT_MATCH_POST);
    }

    // 권한 확인 (작성자만 삭제 가능)
    if (!comment.getMember().getId().equals(memberId)) {
      throw new GeneralException(GeneralErrorCode.COMMENT_FORBIDDEN);
    }

    comment.delete();

    // Post의 댓글 수 감소
    post.decrementCommentCount();

    return CommentConverter.toDeleteCommentResponse(comment);
  }

  /**
   * 댓글 좋아요 토글
   */
  @Transactional
  public CommentResponseDTO.ToggleLikeResponse toggleCommentLike(
      Long postId,
      Long commentId,
      Long memberId
  ) {
    // Post 조회
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.POST_NOT_FOUND));

    // 삭제된 게시글인지 확인
    if (post.getDeletedAt() != null) {
      throw new GeneralException(GeneralErrorCode.POST_NOT_FOUND);
    }

    // Comment 조회
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.COMMENT_NOT_FOUND));

    // 삭제된 댓글인지 확인
    if (comment.getDeletedAt() != null) {
      throw new GeneralException(GeneralErrorCode.COMMENT_NOT_FOUND);
    }

    // 댓글이 해당 게시글에 속하는지 확인
    if (!comment.getPost().getId().equals(postId)) {
      throw new GeneralException(GeneralErrorCode.COMMENT_NOT_MATCH_POST);
    }

    // Member 조회
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

    // 좋아요 존재 여부 확인
    Optional<CommentLike> existingLike = commentLikeRepository.findByCommentIdAndMemberId(commentId, memberId);

    boolean isLiked;

    if (existingLike.isPresent()) {
      // 좋아요 취소
      commentLikeRepository.delete(existingLike.get());
      comment.decrementLikeCount();
      isLiked = false;
    } else {
      // 좋아요 추가
      CommentLike commentLike = CommentLike.builder()
          .comment(comment)
          .member(member)
          .build();
      commentLikeRepository.save(commentLike);
      comment.incrementLikeCount();
      isLiked = true;
    }

    return CommentConverter.toToggleLikeResponse(isLiked, comment.getLikeCount());
  }

  /**
   * 게시글 댓글 목록 조회
   */
  public CommentResponseDTO.CommentListResponse getCommentList(
      Long postId,
      Long memberId,
      Long cursor,
      int size
  ) {
    // Post 조회
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.POST_NOT_FOUND));

    // 삭제된 게시글인지 확인
    if (post.getDeletedAt() != null) {
      throw new GeneralException(GeneralErrorCode.POST_NOT_FOUND);
    }

    // 모든 댓글 조회 (삭제된 것 포함 - 답글 있는 경우 "삭제된 댓글입니다" 표시용)
    List<Comment> allComments = commentRepository.findAll().stream()
        .filter(c -> c.getPost().getId().equals(postId))
        .toList();

    // 최상위 댓글만 필터링 (parentCommentId가 null인 것)
    List<Comment> parentComments = allComments.stream()
        .filter(c -> c.getParent() == null)
        .filter(c -> cursor == null || c.getId() < cursor)
        .filter(c -> {
          // 신고/차단한 원댓글 제외
          boolean isReported = reportRepository.existsByReporterIdAndTargetTypeAndTargetId(
              memberId, ReportTargetType.COMMENT, c.getId()
          );
          boolean isBlocked = memberBlockRepository.existsByBlockerIdAndBlockedId(
              memberId, c.getMember().getId()
          );
          return !isReported && !isBlocked;
        })
        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())) // 최신순
        .limit(size + 1)
        .toList();

    boolean hasNext = parentComments.size() > size;
    List<Comment> content = hasNext
        ? parentComments.subList(0, size)
        : parentComments;

    Long nextCursor = hasNext && !content.isEmpty()
        ? content.get(content.size() - 1).getId()
        : null;

    // 댓글 -> DTO 변환
    List<CommentResponseDTO.CommentDetail> commentDetails = content.stream()
        .map(c -> buildCommentDetail(c, allComments, memberId))
        .toList();

    return CommentResponseDTO.CommentListResponse.builder()
        .comments(commentDetails)
        .hasNext(hasNext)
        .nextCursor(nextCursor)
        .build();
  }

  /**
   * 댓글 DTO 생성
   */
  private CommentResponseDTO.CommentDetail buildCommentDetail(
      Comment comment,
      List<Comment> allComments,
      Long memberId
  ) {
    // 신고/차단 확인
    boolean isReported = reportRepository.existsByReporterIdAndTargetTypeAndTargetId(
        memberId, ReportTargetType.COMMENT, comment.getId()
    );
    boolean isBlocked = memberBlockRepository.existsByBlockerIdAndBlockedId(
        memberId, comment.getMember().getId()
    );

    // 신고/차단한 댓글은 표시 안 함
    if (isReported || isBlocked) {
      return null;
    }

    // 좋아요 여부 확인
    boolean isLiked = commentLikeRepository.existsByCommentIdAndMemberId(
        comment.getId(), memberId
    );

    // 답글 조회 (오래된순)
    List<CommentResponseDTO.CommentDetail> replies = allComments.stream()
        .filter(c -> c.getParent() != null && c.getParent().getId().equals(comment.getId()))
        .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt())) // 오래된순
        .map(reply -> buildCommentDetail(reply, allComments, memberId))
        .filter(reply -> reply != null) // 신고/차단된 답글 제외
        .toList();

    // 삭제된 댓글이면서 답글이 있는 경우
    String content;
    if (comment.getDeletedAt() != null && !replies.isEmpty()) {
      content = "삭제된 댓글입니다.";
    } else if (comment.getDeletedAt() != null) {
      // 삭제되고 답글도 없으면 표시 안함
      return null;
    } else {
      content = comment.getContent();
    }

    // 작성자 정보
    CommentResponseDTO.CommentAuthorInfo authorInfo =
        CommentResponseDTO.CommentAuthorInfo.builder()
            .userId(comment.getMember().getId())
            .nickname(comment.getMember().getNickname())
            .profileImageUrl(comment.getMember().getProfileImageUrl())
            .build();

    return CommentResponseDTO.CommentDetail.builder()
        .commentId(comment.getId())
        .parentCommentId(comment.getParent() != null ? comment.getParent().getId() : null)
        .content(content)
        .likeCount(comment.getLikeCount())
        .isLiked(isLiked)
        .createdAt(comment.getCreatedAt())
        .author(authorInfo)
        .replies(replies)
        .build();
  }
}