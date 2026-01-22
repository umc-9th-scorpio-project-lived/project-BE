package com.lived.domain.member.service;

import com.lived.domain.member.entity.Member;
import com.lived.domain.member.enums.Provider;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.global.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public void updateRefreshToken(String socialId, Provider provider, String refreshToken) {
        Member member = memberRepository.findBySocialIdAndProvider(socialId, provider)
                .orElseThrow(() -> new RuntimeException("해당 소셜 계정으로 가입된 유저가 없습니다."));

        member.updateRefreshToken(refreshToken); // Member 엔티티에 추가한 메서드
    }

    public String reissueAccessToken(Long memberId, String oldRefreshToken) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        //DB에 저장된 리프레시 토큰과 클라이언트가 보낸 토큰이 일치하는지 검증
        if (!oldRefreshToken.equals(member.getRefreshToken())) {
            throw new RuntimeException("리프레시 토큰이 일치하지 않습니다. 다시 로그인하세요.");
        }

        //새로운 토큰 생성하여 반환
        return jwtTokenProvider.createAccessToken(member.getId(), member.getProvider().name());
    }
}
