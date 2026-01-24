package com.lived.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MemberResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "회원가입 완료 응답 DTO")
    public static class SignUpResultDTO {
        @Schema(description = "생성된 회원의 고유 ID", example = "1")
        private Long memberId;

        @Schema(description = "서비스 접근용 Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
        private String accessToken;

        @Schema(description = "토큰 갱신용 Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
        private String refreshToken;

        @Schema(description = "회원가입 완료 시각", example = "2024-03-21T15:30:00")
        private LocalDateTime createdAt;
    }

}
