package com.lived.domain.post.dto;

import com.lived.domain.post.entity.enums.PostCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "게시글 수정 요청")
  public static class UpdatePostRequest {

    @Schema(description = "게시글 카테고리", example = "SELF_LIFE", allowableValues = {"SELF_LIFE", "COUNSEL", "RECOMMEND", "TIP"})
    private PostCategory category;

    @Schema(description = "게시글 제목", example = "수정된 제목")
    private String title;

    @Schema(description = "게시글 본문", example = "수정된 내용")
    private String content;

    @Schema(description = "삭제할 이미지 ID 목록", example = "[1, 2]")
    private List<Long> deleteImageIds;

    @Schema(description = "기존 이미지 순서 재배치", example = "[{\"imageId\":3,\"orderIndex\":1},{\"imageId\":4,\"orderIndex\":2}]")
    private List<ImageOrder> imageOrders;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "이미지 순서 지정")
    public static class ImageOrder {
      @Schema(description = "이미지 ID", example = "3")
      private Long imageId;

      @Schema(description = "순서 (1부터 시작)", example = "1")
      private Integer orderIndex;
    }
  }
}