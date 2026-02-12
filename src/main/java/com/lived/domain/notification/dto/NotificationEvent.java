package com.lived.domain.notification.dto;

import com.lived.domain.member.entity.Member;
import com.lived.domain.notification.enums.TargetType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationEvent {
    private Member receiver;
    private String title;
    private String content;
    private Long targetId;
    private TargetType targetType;
}
