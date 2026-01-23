package com.lived.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberBlockRequestDTO {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "사용자 차단 요청")
  public static class BlockMemberRequest {

    @Schema(description = "차단할 사용자 ID", example = "5")
    @NotNull(message = "차단할 사용자 ID는 필수입니다.")
    private Long blockedMemberId;
  }
}