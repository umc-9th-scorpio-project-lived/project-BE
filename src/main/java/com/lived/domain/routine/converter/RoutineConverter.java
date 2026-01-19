package com.lived.domain.routine.converter;

import com.lived.domain.routine.dto.HomeRoutineResponseDTO;
import com.lived.domain.routine.entity.mapping.MemberRoutine;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RoutineConverter {

    /**
     * 개별 루틴 항목 변환 (Entity + isDone -> RoutineItem)
     */
    public static HomeRoutineResponseDTO.RoutineItem toHomeRoutineItem(MemberRoutine memberRoutine, boolean isDone) {
        return new HomeRoutineResponseDTO.RoutineItem(
                memberRoutine.getId(),
                memberRoutine.getTitle(),
                memberRoutine.getEmoji(),
                memberRoutine.getAlarmTime(),
                isDone
        );
    }

    /**
     * 전체 홈 화면 응답 변환 (List<RoutineItem> + Metadata -> HomeRoutineResponseDTO)
     */
    public static HomeRoutineResponseDTO toHomeRoutineResponseDTO(
            String dateTitle,
            String fullDate,
            String statusMessage,
            List<HomeRoutineResponseDTO.RoutineItem> items) {

        return new HomeRoutineResponseDTO(
                dateTitle,
                fullDate,
                statusMessage,
                items
        );
    }

}
