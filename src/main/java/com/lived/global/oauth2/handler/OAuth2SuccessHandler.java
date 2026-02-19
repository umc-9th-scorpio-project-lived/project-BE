package com.lived.global.oauth2.handler;

import com.lived.domain.member.enums.Provider;
import com.lived.domain.member.service.MemberService;
import com.lived.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    @Value("${app.frontend-url}") // 나중에 수정
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        boolean isNewMember = (boolean) oAuth2User.getAttributes().get("isNewMember");
        String socialId = (String) oAuth2User.getAttributes().get("socialId");
        String name = (String) oAuth2User.getAttributes().get("name");
        String email = (String) oAuth2User.getAttributes().get("email");
        String provider = (String) oAuth2User.getAttributes().get("provider");
        Provider providerEnum = Provider.valueOf(provider.toUpperCase());

        String targetUrl;

        if (isNewMember) {
            //신규 가입자 (회원가입 페이지로 리다이렉트)
            targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/signup/callback")
                    .queryParam("isNewMember", true)
                    .queryParam("socialId", socialId)
                    .queryParam("name", name)
                    .queryParam("email", email)
                    .queryParam("provider", provider)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } else {

            Long memberId = (Long) oAuth2User.getAttributes().get("memberId");

            //기존 가입자 (JWT 토큰 생성)
            String accessToken = jwtTokenProvider.createAccessToken(memberId, provider);
            String refreshToken = jwtTokenProvider.createRefreshToken(memberId);

            // 리프레시 토큰 업데이트
            memberService.updateRefreshToken(socialId, providerEnum, refreshToken);

            // 쿠키 설정
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .path("/")
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .maxAge(1209600) // 쿠키 유효 기간 (2주)
                    .build();

            response.addHeader("Set-Cookie", refreshCookie.toString());

            // 로그인 성공시 리다이렉트
            targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/login/callback")
                    .queryParam("accessToken", accessToken)
                    .queryParam("isNewMember", false)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }
}
