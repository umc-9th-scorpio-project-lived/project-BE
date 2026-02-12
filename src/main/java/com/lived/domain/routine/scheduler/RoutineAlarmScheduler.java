package com.lived.domain.routine.scheduler;

import com.lived.domain.notification.entity.FcmToken;
import com.lived.domain.notification.repository.FcmTokenRepository;
import com.lived.domain.routine.entity.mapping.MemberRoutine;
import com.lived.domain.routine.repository.MemberRoutineRepository;
import com.lived.global.fcm.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoutineAlarmScheduler {

    private final MemberRoutineRepository memberRoutineRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final FcmService fcmService;

    @Scheduled(cron = "0 * * * * *")
    @Transactional(readOnly = true)
    public void processRoutineNotifications() {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDate today = LocalDate.now();

        // 현재 시간에 알림 설정된 루틴 조회
        List<MemberRoutine> candidates = memberRoutineRepository.findRoutinesToNotify(now);

        for (MemberRoutine routine : candidates) {
            // 엔티티 내 반복 로직으로 오늘 실행 대상인지 최종 확인
            if (routine.isScheduledFor(today)) {
                sendNotificationsToAllDevices(routine);
            }
        }
    }

    private void sendNotificationsToAllDevices(MemberRoutine routine) {
        Long memberId = routine.getMember().getId();

        // 해당 회원의 모든 활성 토큰 조회
        List<FcmToken> tokens = fcmTokenRepository.findAllByMemberIdAndIsActiveTrue(memberId);

        if (tokens.isEmpty()) {
            log.warn("사용자(ID: {})의 활성화된 FCM 토큰이 없어 알림을 전송하지 못했습니다.", memberId);
            return;
        }

        String title = "루틴 알림 " + routine.getEmoji();
        String body = String.format("[%s] 루틴을 실천할 시간이에요!", routine.getTitle());

        // 모든 기기(토큰)에 알림 발송
        for (FcmToken fcmToken : tokens) {
            fcmService.sendMessage(fcmToken.getToken(), title, body);
        }

        log.info("사용자(ID: {})에게 루틴 '{}' 알림을 {}개의 기기로 전송했습니다.",
                memberId, routine.getTitle(), tokens.size());
    }
}
