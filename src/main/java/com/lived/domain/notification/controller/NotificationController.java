package com.lived.domain.notification.controller;

import com.lived.domain.notification.dto.NotificationResponseDTO;
import com.lived.domain.notification.enums.TargetType;
import com.lived.domain.notification.service.NotificationService;
import com.lived.global.apiPayload.ApiResponse;
import com.lived.global.apiPayload.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("")
    public ApiResponse<List<NotificationResponseDTO.NotificationDTO>> getNotifications(@RequestParam TargetType targetType) {
        // 사용자 ID 가져오기 추후 구현
        Long memberId = 1L;

        List<NotificationResponseDTO.NotificationDTO> result = notificationService.getNotificationByCategory(memberId, targetType);

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }
}
