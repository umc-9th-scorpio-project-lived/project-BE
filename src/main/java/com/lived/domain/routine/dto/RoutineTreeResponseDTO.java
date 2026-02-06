package com.lived.domain.routine.dto;

import com.lived.domain.routine.enums.FruitType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "루틴 나무 화면 전체 데이터 응답")
public record RoutineTreeResponseDTO(
        @Schema(description = "상단 열매 개수 요약")
        MonthlyFruitSummaryDTO summary,

        @Schema(description = "나무에 표시될 열매 상세 리스트")
        List<FruitItemDTO> fruitList
) {
    @Schema(description = "개별 열매 정보")
    public record FruitItemDTO(
            @Schema(description = "멤버 루틴 ID", example = "101")
            Long memberRoutineId,

            @Schema(description = "열매 타입 (GROWING, NORMAL, GOLD)", example = "GOLD")
            FruitType type
    ) {
    }
}
