package com.lived.domain.routine.service;

import com.lived.domain.member.entity.Member;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.routine.converter.RoutineConverter;
import com.lived.domain.routine.dto.*;
import com.lived.domain.routine.entity.Routine;
import com.lived.domain.routine.entity.RoutineHistory;
import com.lived.domain.routine.enums.RepeatType;
import com.lived.domain.routine.entity.mapping.MemberRoutine;
import com.lived.domain.routine.repository.MemberRoutineRepository;
import com.lived.domain.routine.repository.RoutineHistoryRepository;
import com.lived.domain.routine.repository.RoutineRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final RoutineRepository routineRepository;

    private final RoutineStatisticsService statisticsService;

    @Autowired
    private EntityManager entityManager;

    // 루틴 생성 로직 (커스텀)
    public Long createCustomRoutine(Long memberId, RoutineRequestDTO request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        if (memberRoutineRepository.existsByMemberIdAndTitleAndIsActiveTrue(memberId, request.getTitle())){
            throw new GeneralException(GeneralErrorCode.ROUTINE_ALREADY_EXISTS);
        }

        MemberRoutine newRoutine = MemberRoutine.builder()
                .member(member)
                .title(request.getTitle())
                .emoji(request.getEmoji())
                .repeatType(request.getRepeatType())
                .repeatInterval(request.getRepeatInterval())
                .repeatValue(request.getRepeatValueAsString())
                .isAlarmOn(request.getIsAlarmon())
                .alarmTime(request.getIsAlarmon() ? request.getAlarmTime() : null)
                .startDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now())
                .isActive(true)
                .build();

        return memberRoutineRepository.save(newRoutine).getId();
    }

    // 홈 화면 루틴 조회 로직
    @Transactional(readOnly = true)
    public HomeRoutineResponseDTO getHomeRoutines(Long memberId, LocalDate targetDate) {
        List<MemberRoutine> activeRoutines = memberRoutineRepository.findAllByMemberIdAndIsActiveTrue(memberId);

        // targetDate가 반복 설정에 해당하는지 필터링
        List<MemberRoutine> scheduledRoutines = activeRoutines.stream()
                .filter(mr -> mr.isScheduledFor(targetDate))
                .toList();

        List<Long> routineIds = scheduledRoutines.stream()
                .map(MemberRoutine::getId)
                .toList();

        // 해당 날짜에 각 루틴을 완료했는지 조회
        List<RoutineHistory> histories = routineHistoryRepository.findAllByMemberRoutineIdInAndCheckDate(routineIds, targetDate);

        // Map으로 변환하여 루틴 ID별로 isDone 탐색 빠르게
        Map<Long, Boolean> historyMap = histories.stream()
                .collect(Collectors.toMap(
                        rh -> rh.getMemberRoutine().getId(),
                        RoutineHistory::getIsDone
                ));

        // MemberRoutine -> RoutineItem 변환
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

    // 루틴 상세 정보 조회
    @Transactional(readOnly = true)
    public RoutineDetailResponseDTO getRoutineDetail(Long memberRoutineId) {
        MemberRoutine memberRoutine = memberRoutineRepository.findById(memberRoutineId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.ROUTINE_NOT_FOUND));

        return new RoutineDetailResponseDTO(
                memberRoutine.getId(),
                memberRoutine.getTitle(),
                memberRoutine.getEmoji(),
                memberRoutine.getRepeatType().toString(),
                memberRoutine.getRepeatInterval(),
                memberRoutine.getRepeatValue(),
                memberRoutine.getAlarmTime(),
                memberRoutine.getIsAlarmOn()
        );
    }

    // 루틴 수정
    @Transactional
    public void updateRoutine(Long memberRoutineId, RoutineUpdateRequestDTO request) {
        MemberRoutine memberRoutine = memberRoutineRepository.findById(memberRoutineId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.ROUTINE_NOT_FOUND));

        memberRoutine.update(request);
    }

    // 루틴 삭제
    @Transactional
    public void deleteRoutine(Long memberRoutineId, RoutineDeleteRequestDTO request) {
        MemberRoutine memberRoutine = memberRoutineRepository.findById(memberRoutineId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.ROUTINE_NOT_FOUND));

        switch (request.deleteType()) {
            case ONLY_SET -> {
                // 예외 날짜 리스트에 추가
                memberRoutine.excludeDate(request.targetDate());

                // 이미 생성된 수행 기록이 있다면 DB에서 삭제
                routineHistoryRepository.findByMemberRoutineIdAndCheckDate(memberRoutineId, request.targetDate())
                        .ifPresent(routineHistoryRepository::delete);

                memberRoutineRepository.flush();

                entityManager.clear();
            }
            case AFTER_SET -> memberRoutine.terminateAt(request.targetDate());
            case ALL_SET -> {
                routineHistoryRepository.deleteAllByMemberRoutineId(memberRoutineId);

                memberRoutineRepository.delete(memberRoutine);
            }
        }
    }

    // 루틴 완료상태 변경
    @Transactional
    public boolean toggleRoutineCheck(Long memberRoutineId, LocalDate targetDate) {
        MemberRoutine memberRoutine = memberRoutineRepository.findById(memberRoutineId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.ROUTINE_NOT_FOUND));

        if(targetDate.isAfter(LocalDate.now())) {
            throw new GeneralException(GeneralErrorCode.FUTURE_ROUTINE_CHECK_NOT_ALLOWED);
        }

        if(!memberRoutine.isScheduledFor(targetDate)) {
            throw new GeneralException(GeneralErrorCode.BAD_REQUEST);
        }

        boolean isDone = routineHistoryRepository.findByMemberRoutineIdAndCheckDate(memberRoutineId, targetDate)
                .map(history -> {
                    history.toggleDone();
                    return history.getIsDone();
                })
                .orElseGet(() -> {
                    RoutineHistory newHistory = RoutineHistory.builder()
                            .memberRoutine(memberRoutine)
                            .checkDate(targetDate)
                            .isDone(true)
                            .build();
                    routineHistoryRepository.save(newHistory);
                    return true;
                });

        routineHistoryRepository.flush();
        statisticsService.syncRoutineFruit(memberRoutine, targetDate);

        return isDone;
    }

    // 일괄 선택된 루틴 등록
    @Transactional
    public int registerRoutinesBatch(Long memberId, RoutineBatchAddRequestDTO request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        // 사용자가 이미 등록한 루틴들
        List<Long> existingRoutineIds = memberRoutineRepository.findAllByMemberIdAndIsActiveTrue(memberId)
                .stream()
                .filter(mr -> mr.getRoutine() != null)
                .map(mr -> mr.getRoutine().getId())
                .toList();

        // 이미 등록되어 있는 것들은 빼고 조회
        List<Routine> templates = routineRepository.findAllById(request.routinIds())
                .stream()
                .filter(template -> !existingRoutineIds.contains(template.getId())) // 중복 제거
                .toList();

        if(templates.isEmpty()) {
            return 0;
        }

        List<MemberRoutine> memberRoutines = templates.stream()
                .map(template -> MemberRoutine.builder()
                        .member(member)
                        .routine(template)
                        .title(template.getTitle())
                        .emoji(template.getCategory().getEmoji())
                        .startDate(LocalDate.now())
                        .repeatType(RepeatType.WEEKLY)
                        .repeatValue("0,1,2,3,4,5,6")
                        .isActive(true)
                        .isAlarmOn(false)
                        .build())
                .collect(Collectors.toList());

        memberRoutineRepository.saveAll(memberRoutines);
        return memberRoutines.size();
    }

}
