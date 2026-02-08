package com.lived.domain.member.service;



import com.lived.domain.member.converter.FriendshipConverter;
import com.lived.domain.member.dto.FriendshipResponseDTO;
import com.lived.domain.member.entity.Friendship;
import com.lived.domain.member.entity.Member;
import com.lived.domain.member.enums.FriendshipStatus;
import com.lived.domain.member.enums.MemberStatus;
import com.lived.domain.member.dto.FriendTreeResponseDTO;
import com.lived.domain.member.repository.FriendshipRepository;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.routine.dto.RoutineTreeResponseDTO;
import com.lived.domain.routine.service.RoutineStatisticsService;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendshipService {

    private final MemberRepository memberRepository;
    private final FriendshipRepository friendshipRepository;
    private final RoutineStatisticsService routineStatisticsService;

    /**
     * 친구 목록 조회
     * 수락된 관계만 필터링
     * 탈퇴하지 않은 유저만 포함
     * 닉네임 가나다순 정렬
     */
    public FriendshipResponseDTO.FriendListDTO getFriendList(Long memberId, String name) {
        // 본인 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        // 수락된 친구 관계 조회
        List<Friendship> friendships = friendshipRepository.findAllAcceptedFriendsByName(member, name);

        // 친구 정보
        List<FriendshipResponseDTO.FriendInfoDTO> friendInfoList = friendships.stream()
                .map(f -> f.getRequester().equals(member) ? f.getReceiver() : f.getRequester())
                // 계정이 활성 상태인 친구만 보이게함
                .filter(friend -> friend != null && MemberStatus.ACTIVE.equals(friend.getStatus()))
                .map(FriendshipConverter::toFriendInfoDTO)
                .sorted(Comparator.comparing(FriendshipResponseDTO.FriendInfoDTO::getName))
                .collect(Collectors.toList());

        // 최종 리스트 DTO 반환
        return FriendshipConverter.toFriendListDTO(friendInfoList);
    }

    // 초대 정보
    @Transactional
    public FriendshipResponseDTO.InviteInfoDTO getMyInviteInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));
        return FriendshipConverter.toInviteInfoDTO(member);
    }

    // 초대 수락 로직
    @Transactional
    public FriendshipResponseDTO.AcceptInviteResultDTO acceptInvitation(Long receiverId, Long inviterId) {

        // 자기 자신 초대 방지
        if (receiverId.equals(inviterId)) {
            throw new GeneralException(GeneralErrorCode.INVALID_INVITATION);
        }

        Optional<Friendship> existingFriendship = friendshipRepository.findAnyFriendship(receiverId, inviterId);

        if (existingFriendship.isPresent()) {
            Friendship friendship = existingFriendship.get();

            // 이미 친구인 경우 에러 발생
            if (!friendship.getIsDeleted()) {
                throw new GeneralException(GeneralErrorCode.ALREADY_FRIENDS);
            }

            // 삭제되었던 경우라면 다시 활성화
            friendship.updateIsDeleted(false);

            return FriendshipConverter.toAcceptInviteResultDTO(friendship);
        }

        // 아예 처음 맺는 관계인 경우
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));
        Member inviter = memberRepository.findById(inviterId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        // 친구 관계 저장
        Friendship friendship = friendshipRepository.save(FriendshipConverter.toFriendship(inviter, receiver));

        return FriendshipConverter.toAcceptInviteResultDTO(friendship);
    }

    // 친구 나무 조회 로직
    @Transactional(readOnly = true)
    public FriendTreeResponseDTO getFriendTree(Long myId, Long friendId, int year, int month) {

        // 친구 관계 확인
        if (!friendshipRepository.existsFriendship(myId, friendId)) {
            throw new GeneralException(GeneralErrorCode.FRIENDSHIP_NOT_FOUND);
        }

        // 친구의 프로필 이름 조회
        Member friend = memberRepository.findById(friendId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        // 친구의 루틴 나무 전체 조회
        RoutineTreeResponseDTO treeData = routineStatisticsService.getRoutineTree(friendId, year, month);

        return new FriendTreeResponseDTO(friend.getName(), treeData);
    }

    // 친구 삭제 로직
    @Transactional
    public void deleteFriend(Long myId, Long friendId) {
        // 나와 친구 사이의 활성화된 친구 관계 조회
        Friendship friendship = friendshipRepository.findActiveFriendship(myId, friendId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.FRIENDSHIP_NOT_FOUND));

        // Soft Delete 수행
        friendship.updateIsDeleted(true);
    }
}
