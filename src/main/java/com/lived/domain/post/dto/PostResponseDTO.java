package com.lived.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}