package com.lived.domain.notification.dto;

import com.lived.domain.notification.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class NotificationResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationDTO {
        private Long id;
        private Long memberId;
        private String title;
        private String content;
        private TargetType target;
        private Long targetId;
        private Boolean isRead;
        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationSettingDTO {
        private Boolean allEnabled;
        private Boolean routineEnabled;
        private Boolean communityEnabled;
        private Boolean postLikeEnabled;
        private Boolean commentEnabled;
        private Boolean commentLikeEnabled;
        private Boolean marketingEnabled;
    }
}
