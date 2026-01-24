package com.lived.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentResponseDTO {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "댓글 작성 응답")
  public static class CreateCommentResponse {

    @Schema(description = "생성된 댓글 ID", example = "78")
    private Long commentId;

    @Schema(description = "작성 시각", example = "2026-01-12T09:45:00")
    private LocalDateTime createdAt;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "댓글 수정 응답")
  public static class UpdateCommentResponse {

    @Schema(description = "수정된 댓글 ID", example = "78")
    private Long commentId;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "댓글 삭제 응답")
  public static class DeleteCommentResponse {

    @Schema(description = "삭제된 댓글 ID", example = "78")
    private Long commentId;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "댓글 좋아요 토글 응답")
  public static class ToggleLikeResponse {

    @Schema(description = "토글 후 좋아요 여부", example = "true")
    private Boolean isLiked;

    @Schema(description = "토글 후 좋아요 수", example = "5")
    private Integer likeCount;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "댓글 작성자 정보")
  public static class CommentAuthorInfo {

    @Schema(description = "작성자 ID", example = "22")
    private Long userId;

    @Schema(description = "작성자 닉네임", example = "비비")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://...")
    private String profileImageUrl;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "댓글 상세 정보")
  public static class CommentDetail {

    @Schema(description = "댓글 ID", example = "55")
    private Long commentId;

    @Schema(description = "부모 댓글 ID", example = "null")
    private Long parentCommentId;

    @Schema(description = "댓글 내용", example = "난 어제 배째고 그냥 누워있었더니...")
    private String content;

    @Schema(description = "좋아요 수", example = "1")
    private Integer likeCount;

    @Schema(description = "내가 좋아요 했는지", example = "false")
    private Boolean isLiked;

    @Schema(description = "작성 시각", example = "2026-01-12T09:21:00")
    private LocalDateTime createdAt;

    @Schema(description = "작성자 정보")
    private CommentAuthorInfo author;

    @Schema(description = "답글 목록")
    private List<CommentDetail> replies;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "댓글 목록 조회 응답")
  public static class CommentListResponse {

    @Schema(description = "댓글 목록")
    private List<CommentDetail> comments;

    @Schema(description = "다음 페이지 존재 여부", example = "false")
    private Boolean hasNext;

    @Schema(description = "다음 커서", example = "null")
    private Long nextCursor;
  }
}