package com.lived.domain.notification.repository;

import com.lived.domain.notification.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    // 특정 회원의 알림 설정 정보 조회
    Optional<NotificationSetting> findByMemberId(Long memberId);
}
