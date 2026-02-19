package com.lived.domain.member.service;

import com.lived.domain.member.converter.MemberMyPageConverter;
import com.lived.domain.member.dto.MemberMyPageRequestDTO;
import com.lived.domain.member.dto.MemberMyPageResponseDTO;
import com.lived.domain.member.entity.Member;
import com.lived.domain.member.entity.TreeVisibilityTarget;
import com.lived.domain.member.enums.TreeVisibility;
import com.lived.domain.member.repository.MemberMyPageQueryRepository;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.member.repository.TreeVisibilityTargetRepository;
import com.lived.domain.routine.entity.RoutineBigFruit;
import com.lived.domain.routine.entity.RoutineFruit;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import com.lived.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberMyPageService {

    private final MemberRepository memberRepository;
    private final MemberMyPageQueryRepository queryRepository;
    private final MemberMyPageConverter memberMyPageConverter;
    private final TreeVisibilityTargetRepository treeVisibilityTargetRepository;
    private final S3Service s3Service;

    // 기본 프로필 정보 조회
    public MemberMyPageResponseDTO.MyProfileResponse getMyProfile(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));
        return memberMyPageConverter.toMemberProfileResponse(member);
    }

    // 커뮤니티 프로필 정보 조회
    public MemberMyPageResponseDTO.CommunityProfileResponse getCommunityProfile(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));
        List<RoutineBigFruit> bigFruits = queryRepository.findTop5BigFruits(member);
        return memberMyPageConverter.toCommunityProfileResponse(member, bigFruits);
    }

    // 커뮤니티 프로필 정보 수정
    @Transactional
    public MemberMyPageResponseDTO.CommunityProfileResponse updateCommunityProfile(
            Long memberId,
            MemberMyPageRequestDTO.UpdateCommunityProfileRequest request,
            MultipartFile image) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        // 이미지가 새로 들어온 경우에만 기존 이미지 삭제 후 업로드 진행
        if (image != null && !image.isEmpty()) {
            if (member.getProfileImageUrl() != null) {
                s3Service.deleteFile(member.getProfileImageUrl());
            }
            String newImageUrl = s3Service.uploadMemberImage(image, memberId);
            member.updateProfile(request.getNickname(), newImageUrl);
        } else {
            member.updateProfile(request.getNickname(), member.getProfileImageUrl());
        }

        List<RoutineBigFruit> bigFruits = queryRepository.findTop5BigFruits(member);
        return memberMyPageConverter.toCommunityProfileResponse(member, bigFruits);
    }

    public MemberMyPageResponseDTO.TreeVisibilityResponse getTreeVisibility(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        // 현재 설정된 타겟 목록 조회 (PARTIAL이 아니면 빈 리스트가 반환됨)
        List<TreeVisibilityTarget> targets = treeVisibilityTargetRepository.findAllByMember(member);

        // Converter를 통해 Response DTO로 변환
        return memberMyPageConverter.toTreeVisibilityResponse(member, targets);
    }

    @Transactional
    public MemberMyPageResponseDTO.TreeVisibilityResponse updateTreeVisibility(
            Long memberId,
            MemberMyPageRequestDTO.UpdateTreeVisibilityDTO request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        // 멤버의 공개 범위 Enum 업데이트
        member.updateTreeVisibility(request.getVisibility());

        // 기존 설정된 PARTIAL 초기화
        treeVisibilityTargetRepository.deleteAllByMember(member);

        // PARTIAL인 경우에만 새로운 타겟 리스트 저장
        if (request.getVisibility() == TreeVisibility.PARTIAL && request.getTargetMemberIds() != null) {
            List<TreeVisibilityTarget> newTargets = memberMyPageConverter.toVisibilityTargetList(member, request.getTargetMemberIds());
            treeVisibilityTargetRepository.saveAll(newTargets);
        }

        // 변경된 최신 상태를 다시 조회하여 반환
        List<TreeVisibilityTarget> currentTargets = treeVisibilityTargetRepository.findAllByMember(member);

        return memberMyPageConverter.toTreeVisibilityResponse(member, currentTargets);
    }
}