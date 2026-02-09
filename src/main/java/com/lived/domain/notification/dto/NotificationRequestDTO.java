package com.lived.domain.notification.dto;

import lombok.Getter;

public class NotificationRequestDTO {

    @Getter
    public static class NotificationSettingDTO {
        private Boolean allEnabled;

        private Boolean routineEnabled;
        private Boolean routineReportEnabled;

        private Boolean communityEnabled;
        private Boolean communityHotEnabled;
        private Boolean commentEnabled;

        private Boolean marketingEnabled;
    }

    @Getter
    public static class FcmTokenDTO {
        private String token;
    }
}