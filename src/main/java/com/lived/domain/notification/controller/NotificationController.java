package com.lived.domain.notification.controller;

import com.lived.domain.notification.dto.NotificationRequestDTO;
import com.lived.domain.notification.dto.NotificationResponseDTO;
import com.lived.domain.notification.enums.TargetType;
import com.lived.domain.notification.service.FcmTokenService;
import com.lived.domain.notification.service.NotificationService;
import com.lived.domain.notification.service.NotificationSettingService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import com.lived.global.auth.annotation.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notification", description = "알림 관련 API (FCM 및 알림 설정)")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationSettingService notificationSettingService;
    private final FcmTokenService fcmTokenService;

    @GetMapping("")
    @Operation(summary = "알림 목록 조회 API", description = "특정 카테고리(ROUTINE, COMMUNITY 등)의 알림 목록을 조회합니다.")
    public ApiResponse<List<NotificationResponseDTO.NotificationDTO>> getNotifications(@AuthMember Long memberId, @RequestParam TargetType targetType) {

        List<NotificationResponseDTO.NotificationDTO> result = notificationService.getNotificationByCategory(memberId, targetType);

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @GetMapping("/settings")
    @Operation(summary = "알림 설정 조회 API", description = "사용자의 현재 알림 설정 ON/OFF 상태를 조회합니다.")
    public ApiResponse<NotificationResponseDTO.NotificationSettingDTO> getNotificationSetting(@AuthMember Long memberId) {

        NotificationResponseDTO.NotificationSettingDTO result = notificationSettingService.getNotificationSetting(memberId);

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @PatchMapping("/settings")
    @Operation(summary = "알림 설정 수정 API", description = "사용자의 알림 ON/OFF 상태를 변경합니다.")
    public ApiResponse<NotificationResponseDTO.NotificationSettingDTO> updateNotificationSetting(@AuthMember Long memberId, @RequestBody NotificationRequestDTO.NotificationSettingDTO request) {

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, notificationSettingService.updateNotificationSetting(memberId, request));
    }

    @PostMapping("/token")
    @Operation(summary = "FCM 토큰 등록 API", description = "사용자의 기기별 FCM 토큰을 등록하거나 최신화합니다.")
    public ApiResponse<String> registerFcmToken(@AuthMember Long memberId, @RequestBody NotificationRequestDTO.FcmTokenDTO request) {

        fcmTokenService.registerToken(memberId, request.getToken());

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, "FCM 토큰이 성공적으로 등록되었습니다.");
    }
}
