package com.lived.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


public class FriendshipResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendInfoDTO {
        private Long memberId;      // 해당 친구의 ID
        private String name;    // 리스트에 표시될 이름
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendListDTO {
        private List<FriendInfoDTO> friendList;
        private Integer totalCount; // 친구 총 인원
    }

    // 카톡 공유를 위한 내 정보 응답용
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InviteInfoDTO {
        private Long inviterId;
        private String inviterName;
        private String invitationUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "초대 수락 결과 DTO")
    public static class AcceptInviteResultDTO {
        private Long friendshipId;
        private String inviterName;
        private LocalDateTime connectedAt;
    }
}
