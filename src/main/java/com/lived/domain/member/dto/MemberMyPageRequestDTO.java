package com.lived.domain.member.dto;

import lombok.Getter;

public class MemberMyPageRequestDTO {

    @Getter
    public static class UpdateCommunityProfileRequest {
        private String nickname;
        private String profileImageUrl;
    }
}
