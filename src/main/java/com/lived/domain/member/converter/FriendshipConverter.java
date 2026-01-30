package com.lived.domain.member.converter;

import com.lived.domain.member.dto.FriendshipResponseDTO;
import com.lived.domain.member.entity.Member;

import java.util.List;

public class FriendshipConverter {
    // 단일 친구 정보
    public static FriendshipResponseDTO.FriendInfoDTO toFriendInfoDTO(Member member) {
        return FriendshipResponseDTO.FriendInfoDTO.builder()
                .memberId(member.getId()) // 친구의 ID
                .name(member.getName()) // 리스트에 표시될 이름
                .build();
    }

    // 친구 리스트 전체
    public static FriendshipResponseDTO.FriendListDTO toFriendListDTO(List<FriendshipResponseDTO.FriendInfoDTO> friendInfoList) {
        return FriendshipResponseDTO.FriendListDTO.builder()
                .friendList(friendInfoList)
                .totalCount(friendInfoList.size()) // 총 친구 수
                .build();
    }
}
