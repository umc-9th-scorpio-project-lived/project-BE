package com.lived.domain.member.dto;

import com.lived.domain.member.enums.TreeVisibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class MemberMyPageRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCommunityProfileRequest {
        private String nickname;
        private String profileImageUrl;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTreeVisibilityDTO {
        private TreeVisibility visibility;
        private List<Long> targetMemberIds;
    }
}
