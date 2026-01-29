package com.lived.domain.member.dto;

import com.lived.domain.member.enums.TreeVisibility;
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
        private String profileImageUrl;
        private String livingPeriod;

        private List<MyPageFruitInfo> fruits;
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
    public static class TreeVisibilityResponse {
        private TreeVisibility visibility;
        private List<Long> targetMemberIds;
    }
}
