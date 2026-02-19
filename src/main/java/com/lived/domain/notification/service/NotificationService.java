package com.lived.domain.notification.service;

import com.lived.domain.notification.converter.NotificationConverter;
import com.lived.domain.notification.dto.NotificationResponseDTO;
import com.lived.domain.notification.entity.Notification;
import com.lived.domain.notification.enums.TargetType;
import com.lived.domain.notification.repository.NotificationRepository;
import com.lived.domain.routine.entity.mapping.MemberRoutine;
import com.lived.domain.routine.repository.MemberRoutineRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberRoutineRepository memberRoutineRepository;

    public List<NotificationResponseDTO.NotificationDTO> getNotificationByCategory(Long memberId, TargetType targetType) {

        List<Notification> notifications = notificationRepository.findAllByMemberIdAndTargetOrderByCreatedAtDesc(memberId, targetType);

        List<NotificationResponseDTO.NotificationDTO> dtoList = new ArrayList<>();

        for (Notification notification : notifications) {
            String emoji = "";
            switch (notification.getTarget()) {
                case ROUTINE:
                    emoji = memberRoutineRepository.findById(notification.getTargetId())
                            .map(MemberRoutine::getEmoji)
                            .orElse("📅");
                    break;

                case ROUTINE_REPORT:
                    emoji = "📊";
                    break;

                case ROUTINE_TREE:
                    emoji = "🌳";
                    break;

                case COMMENT:
                    emoji = "💬";
                    break;

                case COMMUNITY_HOT:
                    emoji = "📈";
                    break;

                default:
                    emoji = "🔔";
            }

            dtoList.add(NotificationConverter.toNotificationDTO(notification, emoji));
        }

        return dtoList;
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