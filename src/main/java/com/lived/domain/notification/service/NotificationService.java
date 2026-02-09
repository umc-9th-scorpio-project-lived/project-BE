package com.lived.domain.notification.service;

import com.lived.domain.notification.converter.NotificationConverter;
import com.lived.domain.notification.dto.NotificationResponseDTO;
import com.lived.domain.notification.entity.Notification;
import com.lived.domain.notification.entity.NotificationSetting;
import com.lived.domain.notification.enums.TargetType;
import com.lived.domain.notification.repository.NotificationRepository;
import com.lived.domain.notification.repository.NotificationSettingRepository;
import com.lived.global.apiPayload.code.BaseErrorCode;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<NotificationResponseDTO.NotificationDTO> getNotificationByCategory(Long memberId, TargetType targetType) {
        List<Notification> notifications = notificationRepository.findAllByMemberIdAndTargetOrderByCreatedAtDesc(memberId, targetType);

        List<NotificationResponseDTO.NotificationDTO> dtoNotificationList = new ArrayList<>();

        for(Notification notification : notifications){
            NotificationResponseDTO.NotificationDTO dto = NotificationConverter.toNotificationDTO(notification);
            dtoNotificationList.add(dto);
        }

        return dtoNotificationList;
    }

    @Transactional
    public void readNotification(Long memberId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getMember().getId().equals(memberId)) {
            throw new GeneralException(GeneralErrorCode.NOTIFICATION_FORBIDDEN);
        }

        notification.setIsRead(true);
    }
}
