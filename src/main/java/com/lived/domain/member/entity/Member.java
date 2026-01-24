package com.lived.domain.member.entity;

import com.lived.domain.member.enums.Gender;
import com.lived.domain.member.enums.LivingPeriod;
import com.lived.domain.member.enums.Provider;
import com.lived.domain.member.enums.TreeVisibility;
import com.lived.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 번호

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private Provider provider; // 로그인 서비스 구분

    @Column(name = "social_id", nullable = false, length = 256)
    private String socialId; // 소셜 서비스 유저 식별값

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "nickname", nullable = false, length = 64)
    private String nickname; // 서비스 내 활동명

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender; // 사용자 성별

    @Column(name = "birth", nullable = false)
    private LocalDate birth; // 사용자 생년월일

    @Enumerated(EnumType.STRING)
    @Column(name = "living_period", nullable = false)
    private LivingPeriod livingPeriod; // 사용자 자취연차

    @Builder.Default
    @Column(name = "notification_status", nullable = false)
    private Integer notificationStatus = 0; // 알림수신 상태 (default: 0)

    @Column(name = "agreement_date")
    private LocalDateTime agreementDate; // 알림수신 상태 변경 시점

    @Enumerated(EnumType.STRING)
    @Column(name = "tree_visibility")
    private TreeVisibility treeVisibility; // 루틴나무 공개 범위

    @Column(name = "status", length = 32)
    private String status; // 활성화 상태

    @Column(name = "inactive_date")
    private LocalDateTime inactiveDate; // 비활성화 시각

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;    // ReFresh 토큰 저장

    @Column(name = "profile_image_url")
    private String profileImageUrl; // 프로필 사진

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
