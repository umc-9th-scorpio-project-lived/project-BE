package com.lived.domain.notification.repository;

import com.lived.domain.notification.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    // 특정 멤버가 해당 토큰을 이미 가지고 있는지 확인
    Optional<FcmToken> findByMemberIdAndToken(Long memberId, String token);
}
