package com.lived.domain.member.controller;

import com.lived.domain.member.dto.MemberBlockRequestDTO;
import com.lived.domain.member.dto.MemberBlockResponseDTO;
import com.lived.domain.member.service.MemberBlockService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member")
@RestController
@RequestMapping("/api/members/blocks")
@RequiredArgsConstructor
public class MemberBlockController {

  private final MemberBlockService memberBlockService;

  @Operation(
      summary = "사용자 차단",
      description = "특정 사용자를 차단합니다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "201",
          description = "차단 성공"
      )
  })
  @PostMapping
  public ApiResponse<MemberBlockResponseDTO.BlockMemberResponse> blockMember(
      @Parameter(description = "사용자 ID", required = true, example = "1")
      @RequestHeader("Member-Id") Long memberId,

      @Parameter(description = "차단 요청 데이터", required = true)
      @Valid @RequestBody MemberBlockRequestDTO.BlockMemberRequest request
  ) {
    MemberBlockResponseDTO.BlockMemberResponse response = memberBlockService.blockMember(memberId, request);
    return ApiResponse.onSuccess(GeneralSuccessCode.CREATED, response);
  }
}