package com.lived.domain.member.dto;

import com.lived.domain.member.enums.TreeVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class MemberMyPageResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyProfileResponse {
        private Long memberId;
        private String name;
        private String email;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityProfileResponse {
        private Long memberId;
        private String nickname;
        private String profileImageUrl;
        private String livingPeriod;

        private List<MyPageBigFruitInfo> bigFruits;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageBigFruitInfo {
        private Long fruitId;
        private String bigFruitType;
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
