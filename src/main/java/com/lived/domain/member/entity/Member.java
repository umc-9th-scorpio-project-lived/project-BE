package com.lived.domain.member.entity;

import com.lived.domain.member.enums.*;
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

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "tree_visibility", nullable = false)
    private TreeVisibility treeVisibility = TreeVisibility.PUBLIC; // 루틴나무 공개 범위

    @Enumerated(EnumType.STRING) // DB에는 문자열로 저장되도록 설정
    @Column(name = "status", length = 32)
    private MemberStatus status;

    @Column(name = "inactive_date")
    private LocalDateTime inactiveDate; // 비활성화 시각

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;    // ReFresh 토큰 저장

    @Column(name = "profile_image_url")
    private String profileImageUrl; // 프로필 사진

    @Column(name = "temp_nickname", length = 64)
    private String tempNickname; // 탈퇴 시 원래 닉네임을 임시 저장

    // 리프레시 토큰 업데이트
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // 프로필사진 변경
    public void updateProfile(String nickname, String profileImageUrl) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    // 로그아웃
    public void logout() {
        this.refreshToken = null;
    }

    // 회원탈퇴 직후
    public void withdraw() {
        this.status = MemberStatus.INACTIVE;
        this.inactiveDate = LocalDateTime.now();
        this.refreshToken = null;
        this.tempNickname = this.nickname; // 원래 랜덤 닉네임을 백업
        this.nickname = "탈퇴한 회원" + this.getId(); // 게시글 등에 즉시 반영
    }

    // 회원 탈퇴 30일 전 로그인 시
    public void recover() {
        this.status = MemberStatus.ACTIVE;
        this.inactiveDate = null;
        this.nickname = this.tempNickname; // 백업해둔 랜덤 닉네임으로 복원
        this.tempNickname = null; // 백업 데이터 삭제
    }

    // 회원 탈퇴 30일 후
    public void anonymize() {
        // socialId를 변경하여 재가입 시 신규 유저로 인식되게 함
        this.socialId = "ANONYMOUS_" + this.getId() + "_" + LocalDateTime.now();
        this.name = "탈퇴한 회원" + this.getId();
        this.nickname = "탈퇴한 회원" + this.getId();
        this.tempNickname = null;
        this.birth = LocalDate.of(1900, 1, 1); // 더미 데이터
        this.status = MemberStatus.DELETED; // 완전 삭제 상태 표시
    }
}
