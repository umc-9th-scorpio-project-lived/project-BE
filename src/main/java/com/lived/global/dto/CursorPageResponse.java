package com.lived.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "커서 기반 페이징 응답")
public class CursorPageResponse<T> {

  @Schema(description = "데이터 목록")
  private List<T> content;

  @Schema(description = "다음 페이지 존재 여부", example = "true")
  private Boolean hasNext;

  @Schema(description = "다음 커서 (다음 페이지 요청 시 사용)", example = "100")
  private Long nextCursor;
}