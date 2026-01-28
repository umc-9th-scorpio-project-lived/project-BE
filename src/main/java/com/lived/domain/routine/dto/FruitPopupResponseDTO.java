package com.lived.domain.routine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "열매 클릭 시 나타나는 루틴 요약 팝업 응답")
public record FruitPopupResponseDTO(
        @Schema(description = "루틴 제목", example = "일어나자마자 이불 정리 하기")
        String title,

        @Schema(description = "이번 달 달성률 (%)", example = "90")
        double achievementRate,

        @Schema(description = "해당 월의 전체 트래커 화면으로 가기 위한 정보들")
        Long memberId,
        int year,
        int month,

        @Schema(description = "트래커 화면에서 해당 루틴 위치로 스크롤하기 위한 ID")
        Long memberRoutineId
) {}
