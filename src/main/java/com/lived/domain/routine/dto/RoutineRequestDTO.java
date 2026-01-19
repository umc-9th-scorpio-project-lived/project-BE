package com.lived.domain.routine.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lived.domain.routine.entity.enums.RepeatType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "루틴 추가 요청 DTO")
public class RoutineRequestDTO {

    @Schema(description = "루틴 제목", example = "물 마시기")
    private String title;

    @Schema(description = "루틴 아이콘(이모지)", example = "👍", defaultValue = "👍")
    private String emoji;

    @Schema(description = "반복 주기 타입", example = "WEEKLY", allowableValues = {"WEEKLY, MONTHLY"})
    private RepeatType repeatType;

    @Schema(description = "반복 간격(n주 마다)", example = "1", defaultValue = "1")
    private Integer repeatInterval;

    @Schema(description = "반복 값 (요일: 0~6(일요일 0) / 날짜: 1~31, L(마지막날)", example = "[\"0\", \"2\", \"4\"]")
    private List<String> repeatValues;

    @Schema(description = "알림 여부", example = "true")
    private Boolean isAlarmon;

    @Schema(description = "알림 시간 (HH:mm 형식)", example = "13:30")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime alarmTime;

    public String getRepeatValueAsString() {
        return (repeatValues != null) ? String.join(",", repeatValues) : null;
    }
}
