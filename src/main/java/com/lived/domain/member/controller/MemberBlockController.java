package com.lived.domain.member.controller;

import com.lived.domain.member.dto.MemberBlockRequestDTO;
import com.lived.domain.member.dto.MemberBlockResponseDTO;
import com.lived.domain.member.dto.MemberBlockResponseDTO.BlockedMemberInfo;
import com.lived.domain.member.service.MemberBlockService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import com.lived.global.dto.CursorPageResponse;
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

  @Operation(
      summary = "차단 목록 조회",
      description = "차단한 사용자 목록을 조회합니다. (커서 페이징)"
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "조회 성공"
      )
  })
  @GetMapping
  public ApiResponse<CursorPageResponse<MemberBlockResponseDTO.BlockedMemberInfo>> getBlockList(
      @Parameter(description = "사용자 ID", required = true, example = "1")
      @RequestHeader("Member-Id") Long memberId,

      @Parameter(description = "커서 (다음 페이지 조회 시 이전 응답의 nextCursor 값)", example = "100")
      @RequestParam(required = false) Long cursor
  ) {
    CursorPageResponse<BlockedMemberInfo> response =
        memberBlockService.getBlockList(memberId, cursor);
    return ApiResponse.onSuccess(GeneralSuccessCode.OK, response);
  }

  @Operation(
      summary = "차단 해제",
      description = "차단한 사용자를 차단 해제합니다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "차단 해제 성공"
      )
  })
  @DeleteMapping("/{blockedMemberId}")
  public ApiResponse<MemberBlockResponseDTO.UnblockMemberResponse> unblockMember(
      @Parameter(description = "사용자 ID", required = true, example = "1")
      @RequestHeader("Member-Id") Long memberId,

      @Parameter(description = "차단 해제할 사용자 ID", required = true, example = "5")
      @PathVariable Long blockedMemberId
  ) {
    MemberBlockResponseDTO.UnblockMemberResponse response =
        memberBlockService.unblockMember(memberId, blockedMemberId);
    return ApiResponse.onSuccess(GeneralSuccessCode.OK, response);
  }
}