package com.lived.global.auth;

import com.lived.domain.member.service.MemberService;
import com.lived.global.jwt.JwtTokenProvider;
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

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 refreshToken 꺼내기
        String refreshToken = getCookieValue(request, "refreshToken");

        // 리프레시 토큰 유효성 검사
        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            String socialId = jwtTokenProvider.getSocialId(refreshToken);

            try {
                //새 액세스 토큰 받기
                String newAccessToken = memberService.reissueAccessToken(socialId, refreshToken);

                //새 액세스 토큰을 쿠키에 갱신
                ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                        .path("/").httpOnly(true).secure(false).maxAge(3600).build();

                response.addHeader("Set-Cookie", accessCookie.toString());
                return ResponseEntity.ok("액세스 토큰 재발급 성공");

            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰이 유효하지 않습니다.");
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