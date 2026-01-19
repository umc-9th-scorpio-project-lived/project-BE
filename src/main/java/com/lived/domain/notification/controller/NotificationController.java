package com.lived.domain.notification.controller;

import com.lived.domain.notification.dto.NotificationRequestDTO;
import com.lived.domain.notification.dto.NotificationResponseDTO;
import com.lived.domain.notification.enums.TargetType;
import com.lived.domain.notification.service.NotificationService;
import com.lived.domain.notification.service.NotificationSettingService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationSettingService notificationSettingService;

    @GetMapping("")
    @Operation(summary = "알림 목록 조회 API", description = "특정 카테고리(ROUTINE, COMMUNITY 등)의 알림 목록을 조회합니다.")
    public ApiResponse<List<NotificationResponseDTO.NotificationDTO>> getNotifications(@RequestParam TargetType targetType) {
        // 사용자 ID 가져오기 추후 구현
        Long memberId = 1L;

        List<NotificationResponseDTO.NotificationDTO> result = notificationService.getNotificationByCategory(memberId, targetType);

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @GetMapping("/settings")
    @Operation(summary = "알림 설정 조회 API", description = "사용자의 현재 알림 설정 ON/OFF 상태를 조회합니다.")
    public ApiResponse<NotificationResponseDTO.NotificationSettingDTO> getNotificationSetting() {
        // 사용자 ID 가져오기 추후 구현
        Long memberId = 1L;

        NotificationResponseDTO.NotificationSettingDTO result = notificationSettingService.getNotificationSetting(memberId);

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @PatchMapping("/settings")
    @Operation(summary = "알림 설정 수정 API", description = "사용자의 알림 ON/OFF 상태를 변경합니다.")
    public ApiResponse<NotificationResponseDTO.NotificationSettingDTO> updateNotificationSetting(@RequestBody NotificationRequestDTO.NotificationSettingDTO request) {
        // 사용자 ID 가져오기 추후 구현
        Long memberId = 1L;

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, notificationSettingService.updateNotificationSetting(memberId, request));
    }
}
