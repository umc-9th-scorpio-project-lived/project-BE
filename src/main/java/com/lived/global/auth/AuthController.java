package com.lived.global.auth;

import com.lived.domain.member.dto.MemberResponseDTO;
import com.lived.domain.member.service.MemberService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import com.lived.global.apiPayload.exception.GeneralException;
import com.lived.global.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@Tag(name = "Member")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/reissue")
    @Operation(summary = "액세스 토큰 재발급", description = "쿠키에 저장된 refreshToken을 검증하여, 만료된 액세스 토큰을 새로 발급하고 쿠키를 갱신합니다.")
    public ApiResponse<MemberResponseDTO.ReissueResultDTO> reissue(HttpServletRequest request) {
        // 쿠키에서 refreshToken 꺼내기
        String refreshToken = getCookieValue(request, "refreshToken");

        // 리프레시 토큰 유효성 검사
        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            Long memberId = Long.parseLong(jwtTokenProvider.getPayload(refreshToken));

            MemberResponseDTO.ReissueResultDTO result = memberService.reissueAccessToken(memberId, refreshToken);

            return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
        }
        throw new GeneralException(GeneralErrorCode.INVALID_TOKEN);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}