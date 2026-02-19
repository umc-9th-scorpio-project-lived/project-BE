package com.lived.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class MemberResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyProfileDTO {
        private String name;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityProfileDTO {
        private String nickname;
        private String livingPeriod;
        private List<MemberRoutineFruitDTO> fruits;

        private List<MemberPostPreviewDTO> writtenPosts;
        private List<MemberPostPreviewDTO> commentedPosts;
        private List<MemberPostPreviewDTO> scrappedPosts;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberRoutineFruitDTO {
        private Long fruitId;
        private Long memberRoutine;
        private String fruitImageUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberPostPreviewDTO {
        private Long postId;
        private String category;
        private String title;
        private String contentSummary;
        private Integer likeCount;
        private Integer commentCount;
        private String createdAt;
        private String firstImageUrl;
    }

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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "토큰 재발급 응답 DTO")
    public static class ReissueResultDTO {
        @Schema(description = "재발급한 Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
        private String accessToken;
    }

}
