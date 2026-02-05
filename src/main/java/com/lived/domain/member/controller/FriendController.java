package com.lived.domain.member.controller;

import com.lived.domain.member.dto.FriendTreeResponseDTO;
import com.lived.domain.member.dto.FriendshipResponseDTO;
import com.lived.domain.member.service.FriendshipService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import com.lived.global.auth.annotation.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Friendship", description = "친구 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendshipService friendshipService;

    @GetMapping("")
    @Operation(
            summary = "내 친구 목록 조회 API",
            description = "현재 수락(ACCEPTED)된 상태의 친구 목록을 가나다순으로 조회합니다. 하단 시트에서 친구 이름을 눌러 루틴 나무로 이동할 때 사용합니다."
    )
    public ApiResponse<FriendshipResponseDTO.FriendListDTO> getFriendList(
            @Parameter(hidden = true) @AuthMember Long memberId
    ) {
        FriendshipResponseDTO.FriendListDTO result = friendshipService.getFriendList(memberId);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @GetMapping("/invite")
    @Operation(summary = "내 초대 정보 조회 API",
            description = "카톡 공유 시 필요한 내 ID와 라우팅 URL을 가져옵니다."
    )
    public ApiResponse<FriendshipResponseDTO.InviteInfoDTO> getInviteInfo(
            @Parameter(hidden = true) @AuthMember Long memberId
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, friendshipService.getMyInviteInfo(memberId));
    }

    @PostMapping("/accept/{inviterId}")
    @Operation(
            summary = "친구 초대 수락 실행 API",
            description = "초대 링크를 통해 들어온 유저가 로그인을 마치면 호출합니다."
    )
    public ApiResponse<FriendshipResponseDTO.AcceptInviteResultDTO> acceptInvitation(
            @Parameter(hidden = true) @AuthMember Long memberId, // 로그인한 나
            @PathVariable Long inviterId // 링크에 담겨있던 상대방 ID
    ) {
        FriendshipResponseDTO.AcceptInviteResultDTO result = friendshipService.acceptInvitation(memberId, inviterId);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @GetMapping("/{friendId}/tree")
    @Operation(
            summary = "친구 루틴 나무 조회",
            description = "친구의 루틴나무 데이터를 조회합니다."
    )
    public ApiResponse<FriendTreeResponseDTO> getFriendTree(
            @AuthMember Long myId,
            @PathVariable Long friendId,
            @RequestParam int year,
            @RequestParam int month) {

        FriendTreeResponseDTO response = friendshipService.getFriendTree(myId, friendId, year, month);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, response);
    }
}
