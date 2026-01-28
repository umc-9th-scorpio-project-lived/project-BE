package com.lived.domain.member.controller;

import com.lived.domain.member.dto.FriendshipResponseDTO;
import com.lived.domain.member.service.FriendshipService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import com.lived.global.auth.annotation.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
