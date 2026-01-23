package com.lived.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
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
}