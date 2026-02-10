package com.lived.domain.notification.entity;

import com.lived.domain.member.entity.Member;
import com.lived.domain.notification.dto.NotificationRequestDTO;
import com.lived.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "notification_setting")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NotificationSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder.Default
    @Column(name = "all_enabled", nullable = false)
    private Boolean allEnabled = true; // 전체 알림 설정

    @Builder.Default
    @Column(name = "routine_enabled", nullable = false)
    private Boolean routineEnabled = true; // 루틴 알림

    @Builder.Default
    @Column(name = "stats_enabled", nullable = false)
    private Boolean statsEnabled = true; // 통계 분석 알림 (기존 routineReportEnabled)

    @Builder.Default
    @Column(name = "community_enabled", nullable = false)
    private Boolean communityEnabled = true; // 커뮤니티 알림

    @Builder.Default
    @Column(name = "comment_enabled", nullable = false)
    private Boolean commentEnabled = true; // 댓글 알림

    @Builder.Default
    @Column(name = "hot_post_enabled", nullable = false)
    private Boolean hotPostEnabled = true; // 실시간 인기글 알림

    @Builder.Default
    @Column(name = "marketing_enabled", nullable = false)
    private Boolean marketingEnabled = false; // 마케팅 정보 알림

    public void update(NotificationRequestDTO.NotificationSettingDTO request) {
        // 1. 전체 알림 설정 변경 시 모든 항목 일괄 변경
        if (request.getAllEnabled() != null) {
            this.allEnabled = request.getAllEnabled();
            this.routineEnabled = request.getAllEnabled();
            this.statsEnabled = request.getAllEnabled();
            this.communityEnabled = request.getAllEnabled();
            this.commentEnabled = request.getAllEnabled();
            this.hotPostEnabled = request.getAllEnabled();
            this.marketingEnabled = request.getAllEnabled();
            return;
        }

        // 2. 개별 설정 변경 (null이 아닌 경우에만 업데이트)
        if (request.getRoutineEnabled() != null) this.routineEnabled = request.getRoutineEnabled();
        if (request.getStatsEnabled() != null) this.statsEnabled = request.getStatsEnabled();
        if (request.getCommunityEnabled() != null) this.communityEnabled = request.getCommunityEnabled();
        if (request.getCommentEnabled() != null) this.commentEnabled = request.getCommentEnabled();
        if (request.getHotPostEnabled() != null) this.hotPostEnabled = request.getHotPostEnabled();
        if (request.getMarketingEnabled() != null) this.marketingEnabled = request.getMarketingEnabled();
    }
}