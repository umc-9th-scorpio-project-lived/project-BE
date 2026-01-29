package com.lived.domain.routine.service;

import com.lived.domain.routine.dto.*;
import com.lived.domain.routine.entity.RoutineFruit;
import com.lived.domain.routine.entity.RoutineHistory;
import com.lived.domain.routine.entity.enums.DayStatus;
import com.lived.domain.routine.entity.enums.FruitType;
import com.lived.domain.routine.entity.mapping.MemberRoutine;
import com.lived.domain.routine.repository.MemberRoutineRepository;
import com.lived.domain.routine.repository.RoutineFruitRepository;
import com.lived.domain.routine.repository.RoutineHistoryRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineStatisticsService {

    private final MemberRoutineRepository memberRoutineRepository;
    private final RoutineHistoryRepository routineHistoryRepository;
    private final RoutineFruitRepository routineFruitRepository;

    // 루틴 나무 전체 데이터 화면 반환
    public RoutineTreeResponseDTO getRoutineTree(Long memberId, int year, int month) {
        LocalDate targetMonth = LocalDate.of(year, month, 1);

        MonthlyFruitSummaryDTO summary = getMonthlyFruitSummary(memberId, year, month);

        List<RoutineFruit> fruits = routineFruitRepository.findAllByMemberRoutineMemberIdAndMonth(memberId, targetMonth);

        List<RoutineTreeResponseDTO.FruitItemDTO> fruitList = fruits.stream()
                .map(f -> new RoutineTreeResponseDTO.FruitItemDTO(
                        f.getMemberRoutine().getId(),
                        f.getFruitType()
                ))
                .toList();

        return new RoutineTreeResponseDTO(summary, fruitList);
    }

    // 특정 멤버의 월간 전체 트래커 데이터 조회
    public MonthlyTrackerViewResponseDTO getMonthlyTrackerView(Long memberId, int year, int month) {
        // 1. 해당 멤버의 모든 루틴 조회
        List<MemberRoutine> routines = memberRoutineRepository.findAllByMemberId(memberId);

        // 2. 루틴별 캘린더 데이터 생성
        List<RoutineCalenderResponseDTO> trackerList = routines.stream()
                .map(routine -> getMonthlyCalender(routine, year, month))
                .collect(Collectors.toList());

        // 3. 상단 열매 요약 정보 조회
        MonthlyFruitSummaryDTO fruitSummary = getMonthlyFruitSummary(memberId, year, month);

        return new MonthlyTrackerViewResponseDTO(fruitSummary, trackerList, year, month);
    }

    public RoutineCalenderResponseDTO getMonthlyCalender(MemberRoutine routine, int year, int month) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
        LocalDate today = LocalDate.now();

        // 1. 해당 월의 기록을 한 번에 가져와서 날짜별 Map으로 변환
        Map<LocalDate, RoutineHistory> historyMap = routineHistoryRepository
                .findAllByMemberRoutineIdAndCheckDateBetween(routine.getId(), firstDay, lastDay)
                .stream()
                .collect(Collectors.toMap(RoutineHistory::getCheckDate, h -> h));

        // 2. 1일부터 말일까지 루프를 돌며 날짜별 상태(DayStatus) 판별
        List<RoutineCalenderResponseDTO.DayResponseDTO> days = new ArrayList<>();
        for (int day = 1; day <= firstDay.lengthOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);
            RoutineHistory history = historyMap.get(date);

            DayStatus status;
            if (history != null && history.getIsDone()) {
                status = DayStatus.SUCCESS; // 완료함
            } else if (routine.isScheduledFor(date) && date.isBefore(today)) {
                status = DayStatus.FAIL; // 해야 하는 날인데 안 하고 지나감
            } else {
                status = DayStatus.UNACTIVE; // 미래이거나 애초에 루틴이 없는 날
            }

            days.add(new RoutineCalenderResponseDTO.DayResponseDTO(day, status));
        }

        int rate = (int) calculateAchievementRate(routine, firstDay, lastDay);
        String statusMessage = getRoutineStatusMessage(rate);

        return new RoutineCalenderResponseDTO(
                routine.getId(),
                routine.getTitle(),
                rate,
                statusMessage,
                days
        );
    }


    private String getRoutineStatusMessage(int rate) {
        if (rate >= 90) return "완벽해요! 황금열매를 얻었어요!";
        if (rate >= 60) return "열매가 열렸어요!";
        return "아직 자라나는 중이에요";
    }


    // 월간 전체 열매 개수 집계
    public MonthlyFruitSummaryDTO getMonthlyFruitSummary(Long memberId, int year, int month) {
        LocalDate targetMonth = LocalDate.of(year, month, 1);

        List<RoutineFruit> fruits = routineFruitRepository.findAllByMemberRoutineMemberIdAndMonth(memberId, targetMonth);

        long gold = fruits.stream().filter(f -> f.getFruitType() == FruitType.GOLD).count();
        long normal = fruits.stream().filter(f -> f.getFruitType() == FruitType.NORMAL).count();
        long growing = fruits.stream().filter(f -> f.getFruitType() == FruitType.GROWING).count();

        return new MonthlyFruitSummaryDTO(gold, normal, growing);
    }

    // 개별 루틴의 월간 달성률 계산
    private double calculateAchievementRate(MemberRoutine routine, LocalDate start, LocalDate end) {
        // 해당 월에 수행해야 하는 일 수
        long scheduledDays = Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .filter(routine::isScheduledFor)
                .count();

        // 실제 완료한 일 수
        long completedDays = routineHistoryRepository.countCompletedDays(routine, start, end);

        if(scheduledDays == 0) return 0.0;
        double rate =  ((double) completedDays / scheduledDays) * 100;

        // 소수점 첫째 자리까지 반올림
        return Math.round(rate * 10.0) / 10.0;
    }

    // achievementRate 업데이트
    @Transactional
    public void syncRoutineFruit(MemberRoutine routine, LocalDate date) {
        LocalDate startOfMonth = date.withDayOfMonth(1);
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        double rate = calculateAchievementRate(routine, startOfMonth, endOfMonth);

        // RoutineFruit 조회 또는 생성
        RoutineFruit fruit = routineFruitRepository.findByMemberRoutineIdAndMonth(routine.getId(), startOfMonth)
                .orElseGet(() -> routineFruitRepository.save(
                        RoutineFruit.builder()
                                .memberRoutine(routine)
                                .month(startOfMonth)
                                .build()
                ));

        // 엔티티 내부 로직을 통해 열매 등급 업데이트
        fruit.updateAchievement(rate);
    }

    // 특정 루틴의 팝업용 요약 정보 조회
    public FruitPopupResponseDTO getRoutinePopup(Long memberRoutineId, int year, int month) {
        MemberRoutine routine = memberRoutineRepository.findById(memberRoutineId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.ROUTINE_NOT_FOUND));

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        double rate = calculateAchievementRate(routine, firstDay, lastDay);

        return new FruitPopupResponseDTO(
                routine.getTitle(),
                rate,
                routine.getMember().getId(),
                year,
                month,
                routine.getId()
        );
    }



}
