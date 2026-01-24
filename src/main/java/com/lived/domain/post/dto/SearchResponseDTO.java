package com.lived.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SearchResponseDTO {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "검색어 기록 아이템")
  public static class SearchHistoryItem {

    @Schema(description = "검색 기록 ID", example = "1")
    private Long historyId;

    @Schema(description = "검색어", example = "자취")
    private String keyword;

    @Schema(description = "검색 시각", example = "2026-01-12T09:17:00")
    private LocalDateTime searchedAt;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "검색어 기록 목록 응답")
  public static class SearchHistoryListResponse {

    @Schema(description = "검색어 기록 목록 (최대 10개)")
    private List<SearchHistoryItem> histories;
  }
}