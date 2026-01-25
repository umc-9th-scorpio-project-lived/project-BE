package com.lived.domain.member.controller;

import com.lived.domain.member.dto.MemberMyPageRequestDTO;
import com.lived.domain.member.dto.MemberMyPageResponseDTO;
import com.lived.domain.member.service.MemberMyPageService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import com.lived.global.auth.annotation.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members/me")
@RequiredArgsConstructor
public class MemberMyPageController {

    private final MemberMyPageService memberMyPageService;

    @GetMapping("/community")
    @Operation(summary = "커뮤니티 프로필 기본 정보 조회", description = "닉네임, 자취연차, 획득 열매 5개를 조회합니다.")
    public ApiResponse<MemberMyPageResponseDTO.CommunityProfileResponse> getCommunityProfile(@AuthMember Long memberId) {
        // 첫 번째 인자로 성공 코드를 넘겨줍니다.
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberMyPageService.getCommunityProfile(memberId));
    }

    @PatchMapping("/community")
    @Operation(summary = "커뮤니티 프로필 수정", description = "닉네임, 프로필 사진을 수정합니다.")
    public ApiResponse<MemberMyPageResponseDTO.CommunityProfileResponse> updateCommunityProfile(@AuthMember Long memberId, @RequestBody MemberMyPageRequestDTO.UpdateCommunityProfileRequest request) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberMyPageService.updateCommunityProfile(memberId, request));
    }

    @GetMapping("/posts/written")
    @Operation(summary = "내가 작성한 게시글 조회", description = "내가 작성한 게시글 목록을 최신순으로 조회합니다.")
    public ApiResponse<MemberMyPageResponseDTO.CommunityProfilePostListResponse> getWrittenPosts(@AuthMember Long memberId) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberMyPageService.getWrittenPosts(memberId));
    }

    @GetMapping("/posts/commented")
    @Operation(summary = "내가 댓글 단 게시글 조회", description = "내가 댓글을 단 게시글 목록을 최신순으로 조회합니다.")
    public ApiResponse<MemberMyPageResponseDTO.CommunityProfilePostListResponse> getCommentedPosts(@AuthMember Long memberId) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberMyPageService.getCommentedPosts(memberId));
    }

    @GetMapping("/posts/scrapped")
    @Operation(summary = "내가 스크랩한 게시글 조회", description = "내가 스크랩한 게시글 목록을 최신순으로 조회합니다.")
    public ApiResponse<MemberMyPageResponseDTO.CommunityProfilePostListResponse> getScrappedPosts(@AuthMember Long memberId) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberMyPageService.getScrappedPosts(memberId));
    }
}