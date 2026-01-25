package com.lived.domain.member.controller;

import com.google.api.client.util.SecurityUtils;
import com.lived.domain.member.dto.MemberRequestDTO;
import com.lived.domain.member.dto.MemberResponseDTO;
import com.lived.domain.member.service.MemberService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode; //
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 관련 API (가입 및 온보딩)")
@RestController
@RequestMapping("/api/auth") //
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    // 회원가입
    @PostMapping("/signup")
    @Operation(
            summary = "소셜 회원가입 API",
            description = "소셜 정보와 온보딩 데이터를 받아 최종 회원가입을 처리"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON201",
                    description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON400",
                    description = "잘못된 요청 데이터입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER4001",
                    description = "이미 가입된 회원입니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<MemberResponseDTO.SignUpResultDTO> signup(
            @RequestBody @Valid MemberRequestDTO.SignUpDTO request //
    ) {
        // 회원가입 로직 실행
        MemberResponseDTO.SignUpResultDTO result = memberService.signup(request);

        // SuccessCode의 CREATED를 반환
        return ApiResponse.onSuccess(GeneralSuccessCode.CREATED, result);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃 API",
            description = "현재 로그인된 사용자의 Refresh Token을 삭제하여 로그아웃 처리합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200_1",
                    description = "로그아웃 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER404_1",
                    description = "존재하지 않는 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<String> logout(
            @AuthenticationPrincipal Long memberId // 시큐리티 컨텍스트에서 인증 객체를 바로 가져옴
    ) {
        memberService.logout(memberId);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, "로그아웃 성공");
    }
}