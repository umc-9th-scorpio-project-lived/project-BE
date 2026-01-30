package com.lived.domain.member.service;



import com.lived.domain.member.converter.FriendshipConverter;
import com.lived.domain.member.dto.FriendshipResponseDTO;
import com.lived.domain.member.entity.Friendship;
import com.lived.domain.member.entity.Member;
import com.lived.domain.member.enums.MemberStatus;
import com.lived.domain.member.repository.FriendshipRepository;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendshipService {

    private final MemberRepository memberRepository;
    private final FriendshipRepository friendshipRepository;

    /**
     * 친구 목록 조회
     * 수락된(ACCEPTED) 관계만 필터링
     * 탈퇴하지 않은(ACTIVE) 유저만 포함
     * 닉네임 가나다순 정렬
     */
    public FriendshipResponseDTO.FriendListDTO getFriendList(Long memberId) {
        // 본인 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        // 수락된 친구 관계 조회
        List<Friendship> friendships = friendshipRepository.findAllAcceptedFriends(member);

        // 친구 정보
        List<FriendshipResponseDTO.FriendInfoDTO> friendInfoList = friendships.stream()
                .map(f -> {
                    // 내가 신청자인지 수신자인지에 따라 상대방 추출
                    return f.getRequester().equals(member) ? f.getReceiver() : f.getRequester();
                })
                // 계정이 활성 상태인 친구만 보이게함
                .filter(friend -> MemberStatus.ACTIVE.equals(friend.getStatus()))
                .map(FriendshipConverter::toFriendInfoDTO)
                .sorted(Comparator.comparing(FriendshipResponseDTO.FriendInfoDTO::getName))
                .collect(Collectors.toList());

        // 최종 리스트 DTO 반환
        return FriendshipConverter.toFriendListDTO(friendInfoList);
    }
}
