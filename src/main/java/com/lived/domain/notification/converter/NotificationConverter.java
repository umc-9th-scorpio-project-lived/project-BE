package com.lived.domain.notification.converter;

import com.lived.domain.notification.dto.NotificationResponseDTO;
import com.lived.domain.notification.entity.Notification;
import com.lived.domain.notification.entity.NotificationSetting;

public class NotificationConverter {

    public static NotificationResponseDTO.NotificationDTO toNotificationDTO(Notification notification, String emoji) {
        return NotificationResponseDTO.NotificationDTO.builder()
                .id(notification.getId())
                .memberId(notification.getMember().getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .emoji(emoji)
                .targetId(notification.getTargetId())
                .target(notification.getTarget())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public static NotificationResponseDTO.NotificationSettingDTO toNotificationSettingDTO(NotificationSetting notificationSetting) {
        return NotificationResponseDTO.NotificationSettingDTO.builder()
                .allEnabled(notificationSetting.getAllEnabled())
                .routineEnabled(notificationSetting.getRoutineEnabled())
                .statsEnabled(notificationSetting.getStatsEnabled())
                .communityEnabled(notificationSetting.getCommunityEnabled())
                .commentEnabled(notificationSetting.getCommentEnabled())
                .hotPostEnabled(notificationSetting.getHotPostEnabled())
                .marketingEnabled(notificationSetting.getMarketingEnabled())
                .build();
    }
}