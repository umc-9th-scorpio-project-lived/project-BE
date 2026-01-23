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

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "차단된 사용자 정보")
  public static class BlockedMemberInfo {

    @Schema(description = "차단 ID", example = "123")
    private Long blockId;

    @Schema(description = "차단된 사용자 ID", example = "5")
    private Long memberId;

    @Schema(description = "닉네임", example = "김차단")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://...")
    private String profileImageUrl;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "차단 해제 응답")
  public static class UnblockMemberResponse {

    @Schema(description = "차단 해제된 사용자 ID", example = "5")
    private Long unblockedMemberId;
  }
}