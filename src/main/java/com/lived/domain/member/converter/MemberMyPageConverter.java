package com.lived.domain.member.converter;

import com.lived.domain.member.dto.MemberMyPageResponseDTO;
import com.lived.domain.member.entity.Member;
import com.lived.domain.member.entity.TreeVisibilityTarget;
import com.lived.domain.routine.entity.RoutineFruit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MemberMyPageConverter {

    // 기본 프로필 정보 변환
    public MemberMyPageResponseDTO.MyProfileResponse toMemberProfileResponse(Member member) {
        return MemberMyPageResponseDTO.MyProfileResponse.builder()
                .memberId(member.getId())
                .name(member.getName())
                .build();
    }

    // 커뮤니티 프로필 정보 변환
    public MemberMyPageResponseDTO.CommunityProfileResponse toCommunityProfileResponse(Member member, List<RoutineFruit> fruits) {
        return MemberMyPageResponseDTO.CommunityProfileResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .livingPeriod(member.getLivingPeriod().name())
                .fruits(mapToFruitInfoList(fruits))
                .build();
    }

    private List<MemberMyPageResponseDTO.MyPageFruitInfo> mapToFruitInfoList(List<RoutineFruit> fruits) {
        return fruits.stream()
                .map(fruit -> MemberMyPageResponseDTO.MyPageFruitInfo.builder()
                        .fruitId(fruit.getId())
                        .fruitType(fruit.getFruitType().name())
                        .build())
                .collect(Collectors.toList());
    }

    // 타겟 ID 리스트 -> 엔티티 리스트 변환
    public List<TreeVisibilityTarget> toVisibilityTargetList(Member owner, List<Long> targetMemberIds) {
        return targetMemberIds.stream()
                .map(targetId -> TreeVisibilityTarget.builder()
                        .member(owner)
                        .targetMemberId(targetId)
                        .build())
                .collect(Collectors.toList());
    }

    public MemberMyPageResponseDTO.TreeVisibilityResponse toTreeVisibilityResponse(Member member, List<TreeVisibilityTarget> targets) {
        // Target Entity 리스트에서 ID만 추출
        List<Long> targetIds = targets.stream()
                .map(TreeVisibilityTarget::getTargetMemberId)
                .collect(Collectors.toList());

        return MemberMyPageResponseDTO.TreeVisibilityResponse.builder()
                .visibility(member.getTreeVisibility())
                .targetMemberIds(targetIds)
                .build();
    }
}