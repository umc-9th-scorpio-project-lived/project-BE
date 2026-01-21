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
}