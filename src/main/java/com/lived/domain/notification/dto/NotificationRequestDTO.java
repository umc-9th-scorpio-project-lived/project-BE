package com.lived.domain.notification.dto;

import lombok.Getter;

public class NotificationRequestDTO {

    @Getter
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
