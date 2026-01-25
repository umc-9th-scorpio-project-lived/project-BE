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

    @GetMapping("")
    @Operation(summary = "기본 프로필 정보 조회", description = "사용자 이름을 조회합니다.")
    public ApiResponse<MemberMyPageResponseDTO.MyProfileResponse> getMyProfile(@AuthMember Long memberId) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberMyPageService.getMyProfile(memberId));
    }

    @GetMapping("/community")
    @Operation(summary = "커뮤니티 프로필 기본 정보 조회", description = "닉네임, 자취연차, 획득 열매 5개를 조회합니다.")
    public ApiResponse<MemberMyPageResponseDTO.CommunityProfileResponse> getCommunityProfile(@AuthMember Long memberId) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberMyPageService.getCommunityProfile(memberId));
    }

    @PatchMapping(value = "/community", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "커뮤니티 프로필 수정", description = "닉네임, 프로필 사진을 수정합니다.")
    public ApiResponse<MemberMyPageResponseDTO.CommunityProfileResponse> updateCommunityProfile(
            @AuthMember Long memberId,
            @RequestPart(value = "request") MemberMyPageRequestDTO.UpdateCommunityProfileRequest request,
            @RequestPart(value = "image", required = false) org.springframework.web.multipart.MultipartFile image) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberMyPageService.updateCommunityProfile(memberId, request, image));
    }

    @GetMapping("/posts/written")
    @Operation(summary = "사용자가 작성한 게시글 조회", description = "사용자가 작성한 게시글 목록을 최신순으로 조회합니다.")
    public ApiResponse<MemberMyPageResponseDTO.CommunityProfilePostListResponse> getWrittenPosts(
            @AuthMember Long memberId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberMyPageService.getWrittenPosts(memberId, cursor, size));
    }

    @GetMapping("/posts/commented")
    @Operation(summary = "사용자가 댓글 단 게시글 조회", description = "사용자가 댓글을 단 게시글 목록을 최신순으로 조회합니다.")
    public ApiResponse<MemberMyPageResponseDTO.CommunityProfilePostListResponse> getCommentedPosts(
            @AuthMember Long memberId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberMyPageService.getCommentedPosts(memberId, cursor, size));
    }

    @GetMapping("/posts/scrapped")
    @Operation(summary = "사용자가 스크랩한 게시글 조회", description = "사용자가 스크랩한 게시글 목록을 최신순으로 조회합니다.")
    public ApiResponse<MemberMyPageResponseDTO.CommunityProfilePostListResponse> getScrappedPosts(
            @AuthMember Long memberId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberMyPageService.getScrappedPosts(memberId, cursor, size));
    }
}