package com.lived.domain.notification.service;

import com.lived.domain.member.entity.Member;
import com.lived.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    @Transactional
    public NotificationResponseDTO.NotificationSettingDTO getNotificationSetting(Long memberId) {
        NotificationSetting notificationSetting = notificationSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        // 만약 데이터가 없다면 새로 생성
        if (notificationSetting == null) {
            // 유저 존재 여부 확인
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

            // 기본값으로 새 설정 객체 생성 및 저장
            notificationSetting = notificationSettingRepository.save(
                    NotificationSetting.builder()
                            .member(member)
                            .allEnabled(true)
                            // 필요한 다른 설정 필드들도 여기에 추가
                            .build()
            );
        }

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