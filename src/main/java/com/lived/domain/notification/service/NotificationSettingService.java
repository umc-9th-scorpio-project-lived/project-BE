package com.lived.domain.notification.service;

import com.lived.domain.notification.converter.NotificationConverter;
import com.lived.domain.notification.dto.NotificationRequestDTO;
import com.lived.domain.notification.dto.NotificationResponseDTO;
import com.lived.domain.notification.entity.NotificationSetting;
import com.lived.domain.notification.repository.NotificationSettingRepository;
import com.lived.global.apiPayload.code.BaseErrorCode;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationSettingService {
    private final NotificationSettingRepository notificationSettingRepository;

    public NotificationResponseDTO.NotificationSettingDTO getNotificationSetting(Long memberId) {
        NotificationSetting notificationSetting = notificationSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        return NotificationConverter.toNotificationSettingDTO(notificationSetting);
    }

    @Transactional
    public NotificationResponseDTO.NotificationSettingDTO updateNotificationSetting(Long memberId, NotificationRequestDTO.NotificationSettingDTO request) {
        NotificationSetting notificationSetting = notificationSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        notificationSetting.update(request);

        return NotificationConverter.toNotificationSettingDTO(notificationSetting);
    }
}