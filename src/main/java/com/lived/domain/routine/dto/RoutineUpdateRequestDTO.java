package com.lived.domain.routine.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lived.domain.routine.entity.enums.RepeatType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalTime;
import java.util.List;


@Schema(description = "루틴 수정 요청 DTO")
public record RoutineUpdateRequestDTO(

        @Schema(description = "루틴 이름", example = "일어나자마자 이불 정리하기")
        @NotBlank(message = "루틴 이름은 필수입니다.")
        String title,

        @Schema(description = "이모지", example = "👍")
        String emoji,

        @Schema(description = "반복 타입", example = "MONTHLY")
        RepeatType repeatType,

        @Schema(description = "주 단위 반복 간격", example = "1")
        Integer repeatInterval,

        @Schema(description = "요일/날짜 선택 값 리스트", example = "[\"1\", \"3\", \"L\"]")
        List<String> repeatValues,

        @Schema(description = "알림 시간 (HH:mm 형식)", example = "07:00")
        @JsonFormat(pattern = "HH:mm")
        LocalTime alarmTime,

        @Schema(description = "알림 설정 여부", example = "true")
        Boolean isAlarmOn
) {
        public String getJoinedRepeatValue() {
            return (repeatValues == null || repeatValues.isEmpty()) ? "" : String.join(",", repeatValues);
        }
}
