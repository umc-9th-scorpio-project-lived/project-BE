package com.lived.domain.notification.service;

import com.lived.domain.member.entity.Member;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.notification.entity.FcmToken;
import com.lived.domain.notification.repository.FcmTokenRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FcmTokenService {
    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;

    public void registerToken(Long memberId, String token) {
        // 멤버 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        // 이미 등록된 토큰인지 확인
        Optional<FcmToken> fcmToken = fcmTokenRepository.findByMemberIdAndToken(memberId, token);

        if (fcmToken.isPresent()) {
            // 이미 존재하는 토큰이라면 활성화로 변경
            fcmToken.get().updateActive(true);
        } else {
            //신규 토큰일 경우 생성 후 저장
            FcmToken newToken = FcmToken.builder()
                    .member(member)
                    .token(token)
                    .isActive(true)
                    .build();
            fcmTokenRepository.save(newToken);
        }
    }
}
