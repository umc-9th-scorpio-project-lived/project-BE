package com.lived.domain.post.controller;

import com.lived.domain.post.dto.SearchResponseDTO;
import com.lived.domain.post.service.SearchService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Community")
@RestController
@RequestMapping("/api/posts/search")
@RequiredArgsConstructor
public class SearchController {

  private final SearchService searchService;

  @Operation(
      summary = "검색어 기록 조회",
      description = "최근 검색어 기록을 최대 10개까지 조회합니다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "조회 성공"
      )
  })
  @GetMapping("/history")
  public ApiResponse<SearchResponseDTO.SearchHistoryListResponse> getSearchHistory(
      @Parameter(description = "사용자 ID", required = true, example = "1")
      @RequestHeader("Member-Id") Long memberId
  ) {
    SearchResponseDTO.SearchHistoryListResponse response =
        searchService.getSearchHistory(memberId);
    return ApiResponse.onSuccess(GeneralSuccessCode.OK, response);
  }
}