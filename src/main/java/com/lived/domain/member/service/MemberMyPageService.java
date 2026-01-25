package com.lived.domain.member.service;

import com.lived.domain.member.converter.MemberMyPageConverter;
import com.lived.domain.member.dto.MemberMyPageRequestDTO;
import com.lived.domain.member.dto.MemberMyPageResponseDTO;
import com.lived.domain.member.entity.Member;
import com.lived.domain.member.repository.MemberMyPageQueryRepository;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.post.service.PostService;
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
    private final S3Service s3Service;
    private final PostService postService;

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
        List<RoutineFruit> fruits = queryRepository.findTop5Fruits(member);
        return memberMyPageConverter.toCommunityProfileResponse(member, fruits);
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

        List<RoutineFruit> fruits = queryRepository.findTop5Fruits(member);
        return memberMyPageConverter.toCommunityProfileResponse(member, fruits);
    }
}