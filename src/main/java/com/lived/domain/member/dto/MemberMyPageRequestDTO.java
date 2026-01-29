package com.lived.domain.member.dto;

import com.lived.domain.member.enums.TreeVisibility;
import lombok.Getter;

import java.util.List;

public class MemberMyPageRequestDTO {

    @Getter
    public static class UpdateCommunityProfileRequest {
        private String nickname;
        private String profileImageUrl;
    }

    @Getter
    public static class UpdateVisibilityDTO {
        private TreeVisibility visibility;
        private List<Long> targetMemberIds;
    }
}
