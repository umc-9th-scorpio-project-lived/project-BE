package com.lived.domain.notification.dto;

import lombok.Getter;

public class NotificationRequestDTO {

    @Getter
    public static class NotificationSettingDTO {
        private Boolean allEnabled;         // 전체 알림
        private Boolean routineEnabled;     // 루틴 알림
        private Boolean statsEnabled;       // 통계 분석 알림
        private Boolean communityEnabled;   // 커뮤니티 알림
        private Boolean commentEnabled;     // 댓글 알림
        private Boolean hotPostEnabled;     // 실시간 인기글 알림
        private Boolean marketingEnabled;   // 마케팅 정보 알림
    }

    @Getter
    public static class FcmTokenDTO {
        private String token;
    }
}