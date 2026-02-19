package com.lived.domain.member.converter;

import com.lived.domain.member.dto.FriendshipResponseDTO;
import com.lived.domain.member.entity.Friendship;
import com.lived.domain.member.entity.Member;
import com.lived.domain.member.enums.FriendshipStatus;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
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

    // 초대 정보 DTO 변환 (라우팅 URL 포함)
    public static FriendshipResponseDTO.InviteInfoDTO toInviteInfoDTO(Member member) {
        return FriendshipResponseDTO.InviteInfoDTO.builder()
                .inviterId(member.getId())
                .inviterName(member.getName())
                .invitationUrl("http://saraboni.co.kr/accept-invite?inviterId=" + member.getId())
                .build();
    }

    // 초대 수락 시 엔티티 생성
    public static Friendship toFriendship(Member requester, Member receiver) {
        return Friendship.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendshipStatus.ACCEPTED)
                .isDeleted(false)
                .build();
    }

    // 결과 DTO 변환
    public static FriendshipResponseDTO.AcceptInviteResultDTO toAcceptInviteResultDTO(Friendship friendship) {
        return FriendshipResponseDTO.AcceptInviteResultDTO.builder()
                .friendshipId(friendship.getId())
                .inviterName(friendship.getRequester().getName())
                .connectedAt(LocalDateTime.now())
                .build();
    }
}
