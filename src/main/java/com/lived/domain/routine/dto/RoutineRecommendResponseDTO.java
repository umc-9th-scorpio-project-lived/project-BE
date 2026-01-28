package com.lived.domain.routine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "카테고리별 추천 루틴 응답 DTO")
public class RoutineRecommendResponseDTO {

    private List<CategorySectionDTO> categories;

    @Getter
    @Builder
    @Schema(description = "카테고리 섹션 정보")
    public static class CategorySectionDTO {
        @Schema(description = "카테고리명", example = "생활 습관")
        private String categoryName;

        @Schema(description = "카테고리 이모지", example = "🛏️")
        private String categoryEmoji;

        @Schema(description = "해당 카테고리의 추천 루틴 리스트")
        private List<RecommendItemDTO> routines;
    }

    @Getter
    @Builder
    @Schema(description = "개별 추천 루틴 아이템")
    public static class RecommendItemDTO {
        @Schema(description = "루틴 ID", example = "1")
        private Long routineId;

        @Schema(description = "루틴 제목", example = "정해진 시간에 일어나기")
        private String title;

        @Schema(description = "루틴 이모지(카테고리 이모지와 동일)", example = "🛏️")
        private String emoji;
    }
}
