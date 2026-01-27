package com.lived.domain.routine.service;

import com.lived.domain.routine.dto.RoutineCalenderResponseDTO;
import com.lived.domain.routine.entity.RoutineHistory;
import com.lived.domain.routine.entity.enums.DayStatus;
import com.lived.domain.routine.entity.mapping.MemberRoutine;
import com.lived.domain.routine.repository.MemberRoutineRepository;
import com.lived.domain.routine.repository.RoutineHistoryRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineStatisticsService {

    private final MemberRoutineRepository memberRoutineRepository;
    private final RoutineHistoryRepository routineHistoryRepository;

    // 루틴 월별 트래커 생성
    @Transactional(readOnly = true)
    public RoutineCalenderResponseDTO getMonthlyCalender(Long memberRoutineId, int year, int month) {
        MemberRoutine routine = memberRoutineRepository.findById(memberRoutineId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.ROUTINE_NOT_FOUND));

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
        LocalDate today = LocalDate.now();

        // 해당 월의 기록을 Map으로 변환
        Map<LocalDate, RoutineHistory> historyMap = routineHistoryRepository
                .findAllByMemberRoutineIdAndCheckDateBetween(memberRoutineId, firstDay, lastDay)
                .stream()
                .collect(Collectors.toMap(RoutineHistory::getCheckDate, h -> h));

        // 1일부터 말일까지 상태 판별
        List<RoutineCalenderResponseDTO.DayResponseDTO> days = new ArrayList<>();

        for (int day = 1; day <= firstDay.lengthOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);
            RoutineHistory history = historyMap.get(date);

            DayStatus status;

            if (history != null && history.getIsDone()) {
                status = DayStatus.SUCCESS;
            } else if (routine.isScheduledFor(date) && date.isBefore(today)) {
                status = DayStatus.FAIL;
            } else {
                status = DayStatus.UNACTIVE;
            }

            days.add(new RoutineCalenderResponseDTO.DayResponseDTO(day, status));
        }

        return new RoutineCalenderResponseDTO(
                routine.getId(),
                routine.getTitle(),
                year,
                month,
                days
        );
    }
}
