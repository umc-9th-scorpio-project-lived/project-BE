package com.lived.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberBlockResponseDTO {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "사용자 차단 응답")
  public static class BlockMemberResponse {

    @Schema(description = "차단 ID", example = "123")
    private Long blockId;
  }
}