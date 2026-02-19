package com.lived.global.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FcmService {

    @Async
    public void sendMessage(String fcmToken, String title, String body) {
        // FCM 메시지 객체 생성
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            // Firebase 서버로 전송
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 알림 전송 성공: {}", response);
        } catch(FirebaseMessagingException e) {
            log.error("FCM 알림 전송 중 오류 발생: {}", e.getMessage());
        }
    }
}
