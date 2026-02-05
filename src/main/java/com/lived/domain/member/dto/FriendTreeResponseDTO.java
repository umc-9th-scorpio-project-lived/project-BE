package com.lived.domain.member.dto;

import com.lived.domain.routine.dto.RoutineTreeResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

public record FriendTreeResponseDTO(
        @Schema(description = "친구 이름", example = "박수용")
        String friendName,

        @Schema(description = "나무 전체 데이터")
        RoutineTreeResponseDTO treeData
) {}
