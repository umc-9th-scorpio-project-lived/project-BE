package com.lived.domain.member.dto;

import lombok.*;
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
}
