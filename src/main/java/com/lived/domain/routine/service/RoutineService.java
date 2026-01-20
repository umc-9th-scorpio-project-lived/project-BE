package com.lived.domain.routine.service;

import com.lived.domain.member.entity.Member;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.routine.converter.RoutineConverter;
import com.lived.domain.routine.dto.HomeRoutineResponseDTO;
import com.lived.domain.routine.dto.RoutineRequestDTO;
import com.lived.domain.routine.dto.RoutineUpdateRequestDTO;
import com.lived.domain.routine.entity.RoutineHistory;
import com.lived.domain.routine.entity.mapping.MemberRoutine;
import com.lived.domain.routine.repository.MemberRoutineRepository;
import com.lived.domain.routine.repository.RoutineHistoryRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoutineService {

    private final MemberRoutineRepository memberRoutineRepository;
    private final MemberRepository memberRepository;
    private final RoutineHistoryRepository routineHistoryRepository;

    // 루틴 생성 로직 (커스텀)
    public Long createCustomRoutine(Long memberId, RoutineRequestDTO request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        MemberRoutine newRoutine = MemberRoutine.builder()
                .member(member)
                .title(request.getTitle())
                .emoji(request.getEmoji())
                .repeatType(request.getRepeatType())
                .repeatInterval(request.getRepeatInterval())
                .repeatValue(request.getRepeatValueAsString())
                .isAlarmOn(request.getIsAlarmon())
                .alarmTime(request.getIsAlarmon() ? request.getAlarmTime() : null)
                .startDate(LocalDate.now())
                .isActive(true)
                .build();

        return memberRoutineRepository.save(newRoutine).getId();
    }

    // 홈 화면 루틴 조회 로직
    @Transactional(readOnly = true)
    public HomeRoutineResponseDTO getHomeRoutines(Long memberId, LocalDate targetDate) {
        List<MemberRoutine> activeRoutines = memberRoutineRepository.findAllByMemberIdAndIsActiveTrue(memberId);

        List<MemberRoutine> scheduledRoutines = activeRoutines.stream()
                .filter(mr -> mr.isScheduledFor(targetDate))
                .toList();

        List<Long> routineIds = scheduledRoutines.stream()
                .map(MemberRoutine::getId)
                .toList();

        List<RoutineHistory> histories = routineHistoryRepository.findAllByMemberRoutineIdInAndCheckDate(routineIds, targetDate);

        Map<Long, Boolean> historyMap = histories.stream()
                .collect(Collectors.toMap(
                        rh -> rh.getMemberRoutine().getId(),
                        RoutineHistory::getIsDone
                ));

        List<HomeRoutineResponseDTO.RoutineItem> items = scheduledRoutines.stream()
                .map(mr -> RoutineConverter.toHomeRoutineItem(mr, historyMap.getOrDefault(mr.getId(), false)))
                .toList();

        int total = items.size();
        int completed = (int) items.stream().filter(HomeRoutineResponseDTO.RoutineItem::isDone).count();


        return RoutineConverter.toHomeRoutineResponseDTO(
                calculateDateTitle(targetDate),
                formatFullDate(targetDate),
                generateProgressMessage(total, completed),
                items
        );

    }

    // 상대 날짜 계산
    private String calculateDateTitle(LocalDate targetDate) {
        LocalDate today = LocalDate.now();
        long diff = ChronoUnit.DAYS.between(today, targetDate);
        if (diff == 0) return "오늘";
        if (diff == 1) return "내일";
        if (diff == -1) return "어제";
        return diff > 0 ? diff + "일 후" : Math.abs(diff) + "일 전";
    }

    // 날짜 포맷팅
    private String formatFullDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("EEEE, MM월 dd일", Locale.KOREAN));
    }

    // 진행 상황 메시지
    private String generateProgressMessage(int total, int completed) {
        if (total == 0) return "오늘 루틴을 시작해볼까요?";
        if (completed == 0) return "아직 완료하지 않은 루틴이 있어요!";
        return String.format("오늘 루틴 %d/%d 진행 중!", completed, total);
    }

    // 루틴 수정
    @Transactional
    public void updateRoutine(Long routineId, RoutineUpdateRequestDTO request) {
        MemberRoutine memberRoutine = memberRoutineRepository.findById(routineId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.ROUTINE_NOT_FOUND));

        memberRoutine.update(request);
    }



}
