package com.lived.domain.routine.service;

import com.lived.domain.ai.service.GeminiService;
import com.lived.domain.routine.converter.RoutineStatisticsConverter;
import com.lived.domain.routine.dto.*;
import com.lived.domain.routine.entity.RoutineBigFruit;
import com.lived.domain.routine.entity.RoutineFruit;
import com.lived.domain.routine.entity.RoutineHistory;
import com.lived.domain.routine.enums.BigFruitType;
import com.lived.domain.routine.enums.DayStatus;
import com.lived.domain.routine.enums.FruitType;
import com.lived.domain.routine.entity.mapping.MemberRoutine;
import com.lived.domain.routine.enums.StatisticsType;
import com.lived.domain.routine.repository.MemberRoutineRepository;
import com.lived.domain.routine.repository.RoutineBigFruitRepository;
import com.lived.domain.routine.repository.RoutineFruitRepository;
import com.lived.domain.routine.repository.RoutineHistoryRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineStatisticsService {

    private final MemberRoutineRepository memberRoutineRepository;
    private final RoutineHistoryRepository routineHistoryRepository;
    private final RoutineFruitRepository routineFruitRepository;
    private final RoutineStatisticsConverter routineStatisticsConverter;
    private final RoutineBigFruitRepository routineBigFruitRepository;
    private final GeminiService geminiService;

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

    // 루틴 나무 모아보기
    public RoutineTreeListResponseDTO getRoutineTreeListPaging(Long memberId, int page, int size) {

        YearMonth currentBase = YearMonth.now();

        YearMonth latestYm = currentBase.minusMonths((long) page * size);

        YearMonth oldestYm = latestYm.minusMonths(size - 1);

        LocalDate startDate = oldestYm.atDay(1);
        LocalDate endDate = latestYm.atEndOfMonth();

        List<RoutineFruit> fetchedFruits = routineFruitRepository.findAllByMemberRoutineMemberIdAndMonthBetween(memberId, startDate, endDate);

        boolean hasNext = oldestYm.getYear() >= 2025;

        return routineStatisticsConverter.toRoutineTreePagingResponseDTO(fetchedFruits, size, page, hasNext);
    }

    public RoutineStatisticsResponseDTO getMyStatistics(Long memberId, int year, int month, Integer week, StatisticsType type) {

        LocalDate startDate;
        LocalDate endDate;
        String periodTitle;

        if (type == StatisticsType.WEEKLY) {
            if (week == null) {
                week = 1;
            }

            LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
            LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());

            LocalDate firstSaturday = firstDayOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

            if (week == 1) {
                startDate = firstDayOfMonth;
                endDate = firstSaturday;
            } else {
                LocalDate weekStart = firstSaturday.plusDays(1).plusWeeks(week - 2);
                startDate = weekStart;
                endDate = weekStart.plusDays(6);
            }

            if (endDate.isAfter(lastDayOfMonth)) {
                endDate = lastDayOfMonth;
            }

            if (startDate.isAfter(lastDayOfMonth)) {
                startDate = lastDayOfMonth;
                endDate = lastDayOfMonth;
            }

            periodTitle = month + "월 " + week + "주차";
        } else {
            YearMonth yearMonth = YearMonth.of(year, month);
            startDate = yearMonth.atDay(1);
            endDate = yearMonth.atEndOfMonth();
            periodTitle = year + "년 " + month + "월";
        }

        List<RoutineHistory> histories = routineHistoryRepository.findAllByMemberIdAndDateBetween(memberId, startDate, endDate);
        List<MemberRoutine> myRoutines = memberRoutineRepository.findAllByMemberId(memberId);

        int periodTotalScheduled = 0;
        int periodTotalDone = 0;
        List<RoutineStatisticsResponseDTO.DailyStatisticsDTO> dailyStats = new ArrayList<>();

        Map<LocalDate, List<RoutineHistory>> historyMap = histories.stream()
                .collect(Collectors.groupingBy(RoutineHistory::getCheckDate));

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate currentDate = date;

            long scheduledCount = myRoutines.stream().filter(r -> r.isScheduledFor(currentDate)).count();

            List<RoutineHistory> dayHistories = historyMap.getOrDefault(currentDate, Collections.emptyList());
            long doneCount = dayHistories.stream().filter(RoutineHistory::getIsDone).count();

            periodTotalScheduled += scheduledCount;
            periodTotalDone += doneCount;

            int dailyRate = (scheduledCount == 0) ? 0 : (int) ((double) doneCount / scheduledCount * 100);

            dailyStats.add(RoutineStatisticsResponseDTO.DailyStatisticsDTO.builder()
                    .date(currentDate)
                    .dayOfWeek(currentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREA))
                    .percentage(dailyRate)
                    .isDone(dailyRate == 100 && scheduledCount > 0)
                    .build());
        }

        int totalRate = (periodTotalScheduled == 0) ? 0 : (int) ((double) periodTotalDone / periodTotalScheduled * 100);


        List<RoutineBigFruit> allFruits = routineBigFruitRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);

        List<RoutineStatisticsResponseDTO.BigFruitDTO> bigFruitPreviewList = allFruits.stream()
                .limit(2)
                .map(this::toBigFruitDTO)
                .toList();

        // gemini
        String prompt = createPrompt(type, periodTitle, totalRate, dailyStats);
        String aiAdvice = geminiService.getAiAdvice(prompt);

        return RoutineStatisticsResponseDTO.builder()
                .type(type)
                .periodTitle(periodTitle)
                .aiAdvice(aiAdvice) // Mock
                .completionRate(RoutineStatisticsResponseDTO.CompleteRateDTO.builder()
                        .percentage(totalRate)
                        .doneCount(periodTotalDone)
                        .totalCount(periodTotalScheduled)
                        .build())
                .dailyGraph(dailyStats)
                .bigFruits(bigFruitPreviewList)
                .build();
    }

    public BigFruitListResponseDTO getAllBigFruits(Long memberId) {
        List<RoutineBigFruit> allFruits = routineBigFruitRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);

        List<RoutineStatisticsResponseDTO.BigFruitDTO> bigFruitList = allFruits.stream()
                .map(this::toBigFruitDTO)
                .toList();

        return BigFruitListResponseDTO.builder()
                .fruits(bigFruitList)
                .build();
    }

    private RoutineStatisticsResponseDTO.BigFruitDTO toBigFruitDTO(RoutineBigFruit fruit) {
        int percent = (fruit.getGoalValue() == 0) ? 0 :
                (int) ((double) fruit.getCurrentValue() / fruit.getGoalValue() * 100);

        String desc = (fruit.getType() == BigFruitType.STREAK)
                ? "최대 연속일 " + fruit.getGoalValue() + "일 달성"
                : fruit.getGoalValue() + "개 완료";

        return RoutineStatisticsResponseDTO.BigFruitDTO.builder()
                .id(fruit.getId())
                .fruitType(fruit.getType())
                .currentValue(fruit.getCurrentValue())
                .goalValue(fruit.getGoalValue())
                .percentage(percent)
                .description(desc)
                .build();
    }

    private String createPrompt(StatisticsType type, String periodTitle, int totalRate, List<RoutineStatisticsResponseDTO.DailyStatisticsDTO> dailyStats) {
        StringBuilder sb = new StringBuilder();
        sb.append("너는 사용자의 루틴 관리를 도와주는 AI 코치야.\n");
        sb.append("아래 데이터를 보고 사용자에게 조언을 해줘.\n\n");

        sb.append("[분석 기간]: ").append(periodTitle).append("\n");
        sb.append("[전체 달성률]: ").append(totalRate).append("%\n");

        if (type == StatisticsType.WEEKLY) {
            sb.append("[요일별 달성률]:\n");
            for (RoutineStatisticsResponseDTO.DailyStatisticsDTO day : dailyStats) {
                sb.append("- ").append(day.getDayOfWeek()).append(": ").append(day.getPercentage()).append("%\n");
            }
        }

        sb.append("\n[제약 조건]:\n");
        sb.append("1. 이모지나 인사말을 빼고, 핵심만 말해줘.\n");
        sb.append("2. 정확히 '두 문장'으로만 답변해줘.\n");
        sb.append("3. 첫 번째 문장은 데이터에 대한 팩트(감소/증가 등)를, 두 번째 문장은 제안이나 격려를 해줘.\n");
        sb.append("4. 예시: \"월요일의 루틴 완료율이 줄어들었어요. 루틴을 조금 조정해보는 건 어때요?\"");

        return sb.toString();
    }

    // 대형 열매 리스트 페이징 조회
    public BigFruitListResponseDTO getBigFruitsPaging(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<RoutineBigFruit> fruitPage = routineBigFruitRepository.findAllByMemberId(memberId, pageable);

        List<RoutineStatisticsResponseDTO.BigFruitDTO> bigFruitList = fruitPage.getContent().stream()
                .map(this::toBigFruitDTO)
                .toList();

        return BigFruitListResponseDTO.builder()
                .hasNext(fruitPage.hasNext())
                .currentPage(page)
                .fruits(bigFruitList)
                .build();
    }
}
