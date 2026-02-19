package com.lived.domain.notification.service;

import com.lived.domain.member.entity.Member;
import com.lived.domain.notification.dto.NotificationEvent;
import com.lived.domain.notification.entity.FcmToken;
import com.lived.domain.notification.entity.Notification;
import com.lived.domain.notification.entity.NotificationSetting;
import com.lived.domain.notification.enums.TargetType;
import com.lived.domain.notification.repository.FcmTokenRepository;
import com.lived.domain.notification.repository.NotificationRepository;
import com.lived.domain.notification.repository.NotificationSettingRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import com.lived.global.fcm.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
    private final FcmService fcmService;
    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final FcmTokenRepository fcmTokenRepository;

    @Async
    @EventListener
    @Transactional
    public void handleNotificationEvent(NotificationEvent event) {
        Member receiver = event.getReceiver();

        // 설정 정보 조회
        NotificationSetting setting = notificationSettingRepository.findByMemberId(receiver.getId())
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        // 전체 알림 마스터 스위치 체크
        if (!setting.getAllEnabled()) return;

        // TargetType별 세부 필드 매핑 체크
        if (!isNotificationEnabled(event.getTargetType(), setting)) {
            log.info("사용자 {}가 {} 알림 수신을 거부했습니다.", receiver.getId(), event.getTargetType());
            return;
        }

        // DB 저장
        Notification notification = Notification.builder()
                .member(receiver)
                .title(event.getTitle())
                .content(event.getContent())
                .targetId(event.getTargetId())
                .target(event.getTargetType())
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        // FCM 발송
        List<FcmToken> tokens = fcmTokenRepository.findAllByMemberIdAndIsActiveTrue(receiver.getId());
        for (FcmToken fcmToken : tokens) {
            fcmService.sendMessage(fcmToken.getToken(), event.getTitle(), event.getContent());
        }
    }

    /**
     * Enum과 Entity 필드를 1:1 혹은 N:1로 매핑
     */
    private boolean isNotificationEnabled(TargetType type, NotificationSetting setting) {
        return switch (type) {
            // 루틴 그룹
            case ROUTINE, ROUTINE_ALARM, ROUTINE_TREE -> setting.getRoutineEnabled();

            // 통계/리포트 그룹
            case ROUTINE_REPORT -> setting.getStatsEnabled();

            // 커뮤니티 그룹
            case COMMUNITY -> setting.getCommunityEnabled();
            case COMMENT -> setting.getCommentEnabled();
            case COMMUNITY_HOT -> setting.getHotPostEnabled(); // 엔티티의 hotPostEnabled와 매핑

            // 마케팅
            case MARKETING -> setting.getMarketingEnabled();

            default -> true;
        };
    }
}