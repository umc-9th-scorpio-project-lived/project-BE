package com.lived.domain.report.controller;

import com.lived.domain.report.dto.ReportRequestDTO;
import com.lived.domain.report.dto.ReportResponseDTO;
import com.lived.domain.report.service.ReportService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Report")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;

  @Operation(
      summary = "게시글/댓글 신고",
      description = "게시글 또는 댓글을 신고합니다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "201",
          description = "신고 생성 성공"
      )
  })
  @PostMapping
  public ApiResponse<ReportResponseDTO.CreateReportResponse> createReport(
      @Parameter(description = "사용자 ID", required = true, example = "1")
      @RequestHeader("Member-Id") Long memberId,

      @Parameter(description = "신고 생성 요청 데이터", required = true)
      @Valid @RequestBody ReportRequestDTO.CreateReportRequest request
  ) {
    ReportResponseDTO.CreateReportResponse response = reportService.createReport(memberId, request);
    return ApiResponse.onSuccess(GeneralSuccessCode.CREATED, response);
  }
}