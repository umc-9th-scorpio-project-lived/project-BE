package com.lived.domain.member.dto;

import com.lived.domain.member.enums.Gender;
import com.lived.domain.member.enums.LivingPeriod;
import com.lived.domain.member.enums.Provider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.LocalDate;
import java.util.List;

public class MemberRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SignUpDTO {
        // 소셜 로그인을 통한 정보
        @NotBlank(message = "소셜 ID는 필수입니다.")
        private String socialId;

        @NotNull(message = "로그인 제공자(카카오/구글)는 필수입니다.")
        private Provider provider;

        @NotBlank(message = "이름은 필수입니다.")
        private String name;

        // 회원가입 페이지에서 입력받는 정보
        @NotNull(message = "자취 연차 정보는 필수입니다.")
        private LivingPeriod livingPeriod;

        @NotNull(message = "성별은 필수입니다.")
        private Gender gender;

        @NotNull(message = "생년월일은 필수입니다.")
        private LocalDate birth;

        // 온보딩 선택 정보 (매핑 테이블 저장용)
        @Schema(description = "선택한 고민 ID 리스트", example = "[1, 2, 3]")
        private List<Long> concernIds;

        @Schema(description = "선택한 추천 루틴 ID 리스트", example = "[5, 10, 12]")
        private List<Long> routineIds;

        @NotNull(message = "알림 수신 동의 여부는 필수입니다.")
        @Schema(description = "알림 수신 동의 여부 (온보딩 팝업 결과)", example = "1")
        private Integer notificationStatus;
    }
}