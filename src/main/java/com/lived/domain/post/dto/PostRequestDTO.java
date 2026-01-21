package com.lived.domain.post.dto;

import com.lived.domain.post.entity.enums.PostCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PostRequestDTO {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "게시글 작성 요청")
  public static class CreatePostRequest {

    @Schema(description = "게시글 카테고리", example = "SELF_LIFE", allowableValues = {"SELF_LIFE", "COUNSEL", "RECOMMEND", "TIP"})
    @NotNull(message = "카테고리는 필수입니다.")
    private PostCategory category;

    @Schema(description = "게시글 제목", example = "게시글 제목")
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @Schema(description = "게시글 본문", example = "게시물 본문")
    @NotBlank(message = "본문은 필수입니다.")
    private String content;
  }
}