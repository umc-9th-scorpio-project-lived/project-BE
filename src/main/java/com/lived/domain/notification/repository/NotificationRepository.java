package com.lived.domain.notification.repository;

import com.lived.domain.notification.entity.Notification;
import com.lived.domain.notification.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.annotation.Target;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 특정 회원의 선택한 카테고리 알림만 최신순으로 조회
    List<Notification> findAllByMemberIdAndTargetOrderByCreatedAtDesc(Long memberId, TargetType target);
}
