package com.lived.domain.post.dto;

import com.lived.domain.post.entity.enums.PostCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.*;

public class PostResponseDTO {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "게시글 작성 응답")
  public static class CreatePostResponse {

    @Schema(description = "생성된 게시글 ID", example = "123")
    private Long postId;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "게시글 수정 응답")
  public static class UpdatePostResponse {

    @Schema(description = "수정된 게시글 ID", example = "123")
    private Long postId;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "게시글 삭제 응답")
  public static class DeletePostResponse {

    @Schema(description = "삭제된 게시글 ID", example = "123")
    private Long postId;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "게시글 좋아요 토글 응답")
  public static class ToggleLikeResponse {

    @Schema(description = "토글 후 좋아요 여부", example = "true")
    private Boolean isLiked;

    @Schema(description = "토글 후 좋아요 수", example = "1")
    private Integer likeCount;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "게시글 스크랩 토글 응답")
  public static class ToggleScrapResponse {

    @Schema(description = "토글 후 스크랩 여부", example = "true")
    private Boolean isScrapped;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "게시글 목록 아이템")
  public static class PostListItem {

    @Schema(description = "게시글 ID", example = "123")
    private Long postId;

    @Schema(description = "카테고리", example = "SELF_LIFE")
    private PostCategory category;

    @Schema(description = "카테고리 라벨", example = "자취 일상")
    private String categoryLabel;

    @Schema(description = "제목", example = "오늘 누워있는데")
    private String title;

    @Schema(description = "내용", example = "갑자기 엄마가 반찬 갖고 온다고 해서 놀랬음...")
    private String content;

    @Schema(description = "좋아요 수", example = "2")
    private Integer likeCount;

    @Schema(description = "댓글 수", example = "4")
    private Integer commentCount;

    @Schema(description = "썸네일 URL (첫 번째 이미지)", example = "https://...")
    private String thumbnailUrl;

    @Schema(description = "이미지 개수", example = "0")
    private Integer imageCount;

    @Schema(description = "차단 여부", example = "false")
    private Boolean isBlocked;

    @Schema(description = "작성 시각", example = "2026-01-12T09:17:00+09:00")
    private LocalDateTime createdAt;
  }
}