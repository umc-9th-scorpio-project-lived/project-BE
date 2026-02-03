package com.lived.domain.member.controller;

import com.lived.domain.member.dto.AnnouncementResponseDTO;
import com.lived.domain.member.dto.MemberMyPageRequestDTO;
import com.lived.domain.member.dto.MemberMyPageResponseDTO;
import com.lived.domain.member.service.AnnouncementService;
import com.lived.domain.member.service.MemberMyPageService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import com.lived.global.auth.annotation.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MyPage", description = "기본 및 커뮤니티 마이페이지 관련 API (프로필 조회 및 수정) ")
@RestController
@RequestMapping("/api/members/me")
@RequiredArgsConstructor
public class MemberMyPageController {

    private final MemberMyPageService memberMyPageService;
    private final AnnouncementService announcementService;

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

    @GetMapping("/visibility")
    @Operation(summary = "루틴 나무 공개 범위 조회", description = "")
    public ApiResponse<MemberMyPageResponseDTO.TreeVisibilityResponse> getTreeVisibility(@AuthMember Long memberId) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberMyPageService.getTreeVisibility(memberId));
    }

    @PatchMapping("/visibility")
    @Operation(summary = "루틴 나무 공개 범위 설정", description = "공개 범위(FRIENDS, PARTIAL, PRIVATE)를 수정합니다. PARTIAL일 경우 targetMemberIds 리스트가 필요합니다.")
    public ApiResponse<MemberMyPageResponseDTO.TreeVisibilityResponse> updateTreeVisibility(
            @AuthMember Long memberId,
            @RequestBody MemberMyPageRequestDTO.UpdateTreeVisibilityDTO request) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberMyPageService.updateTreeVisibility(memberId, request));
    }

    @GetMapping("/announcements")
    @Operation(summary = "공지사항 목록 조회", description = "전체 공지사항 목록을 최신순으로 조회합니다.")
    public ApiResponse<AnnouncementResponseDTO.AnnouncementListDTO> getAnnouncementList() {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, announcementService.getAnnouncementList());
    }
}