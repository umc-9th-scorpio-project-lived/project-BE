package com.lived.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CommentRequestDTO {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "댓글 작성 요청")
  public static class CreateCommentRequest {

    @Schema(description = "댓글 내용", example = "댓글 내용입니다")
    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;

    @Schema(description = "부모 댓글 ID (대댓글일 때만)", example = "55")
    private Long parentCommentId;
  }
}