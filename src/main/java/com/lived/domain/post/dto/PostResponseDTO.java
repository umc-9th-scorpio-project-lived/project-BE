package com.lived.domain.post.dto;

import com.lived.domain.post.entity.enums.PostCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
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

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "실시간 인기글 아이템")
  public static class PopularPostItem {

    @Schema(description = "게시글 ID", example = "101")
    private Long postId;

    @Schema(description = "제목", example = "제목")
    private String title;

    @Schema(description = "내용", example = "내용")
    private String content;

    @Schema(description = "좋아요 수", example = "23")
    private Integer likeCount;

    @Schema(description = "댓글 수", example = "32")
    private Integer commentCount;

    @Schema(description = "작성 시각", example = "2026-01-12T08:30:00")
    private LocalDateTime createdAt;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "실시간 인기글 목록 응답")
  public static class PopularPostListResponse {

    @Schema(description = "인기글 목록")
    private List<PopularPostItem> content;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "게시글 이미지 정보")
  public static class PostImageInfo {

    @Schema(description = "이미지 ID", example = "1")
    private Long imageId;

    @Schema(description = "이미지 URL", example = "https://...")
    private String imageUrl;

    @Schema(description = "이미지 순서", example = "1")
    private Integer orderIndex;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "작성자 정보")
  public static class AuthorInfo {

    @Schema(description = "작성자 ID", example = "1")
    private Long userId;

    @Schema(description = "작성자 닉네임", example = "김자취")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://...")
    private String profileImageUrl;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "게시글 상세 조회 응답")
  public static class PostDetailResponse {

    @Schema(description = "게시글 ID", example = "123")
    private Long postId;

    @Schema(description = "카테고리", example = "SELF_LIFE")
    private PostCategory category;

    @Schema(description = "카테고리 라벨", example = "자취 일상")
    private String categoryLabel;

    @Schema(description = "제목", example = "제목")
    private String title;

    @Schema(description = "본문", example = "본문")
    private String content;

    @Schema(description = "조회수", example = "10")
    private Integer viewCount;

    @Schema(description = "좋아요 수", example = "2")
    private Integer likeCount;

    @Schema(description = "댓글 수", example = "4")
    private Integer commentCount;

    @Schema(description = "내가 좋아요 했는지", example = "true")
    private Boolean isLiked;

    @Schema(description = "내가 스크랩 했는지", example = "false")
    private Boolean isScrapped;

    @Schema(description = "작성 시각", example = "2026-01-12T09:17:00")
    private LocalDateTime createdAt;

    @Schema(description = "작성자 정보")
    private AuthorInfo author;

    @Schema(description = "이미지 목록")
    private List<PostImageInfo> images;
  }
}