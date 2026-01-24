package com.lived.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class MemberMyPageResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyProfileResponse {
        private String name;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityProfileResponse {
        private String nickname;
        private String livingPeriod;

        private List<MyPageFruitInfo> fruits;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityProfilePostListResponse {
        private List<MyPagePostPreview> posts;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageFruitInfo {
        private Long fruitId;
        private String fruitType;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPagePostPreview {
        private Long postId;
        private String category;
        private String title;
        private String contentSummary;
        private Integer likeCount;
        private Integer commentCount;
        private String createdAt;
        private String firstImageUrl;
    }
}
