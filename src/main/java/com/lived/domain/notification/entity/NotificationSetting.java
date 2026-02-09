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
    private Boolean allEnabled = true;

    @Builder.Default
    @Column(name = "routine_enabled", nullable = false)
    private Boolean routineEnabled = true;

    @Builder.Default
    @Column(name = "routine_report_enabled", nullable = false)
    private Boolean routineReportEnabled = true;

    @Builder.Default
    @Column(name = "community_enabled", nullable = false)
    private Boolean communityEnabled = true;

    @Builder.Default
    @Column(name = "community_hot_enabled", nullable = false)
    private Boolean communityHotEnabled = true;

    @Builder.Default
    @Column(name = "comment_enabled", nullable = false)
    private Boolean commentEnabled = true;

    @Builder.Default
    @Column(name = "marketing_enabled", nullable = false)
    private Boolean marketingEnabled = false;

    public void update(NotificationRequestDTO.NotificationSettingDTO request) {
        if (request.getAllEnabled() != null) {
            this.allEnabled = request.getAllEnabled();

            this.routineEnabled = request.getAllEnabled();
            this.routineReportEnabled = request.getAllEnabled();
            this.communityEnabled = request.getAllEnabled();
            this.communityHotEnabled = request.getAllEnabled();
            this.commentEnabled = request.getAllEnabled();
            this.marketingEnabled = request.getAllEnabled();

            return;
        }

        if (request.getRoutineEnabled() != null) this.routineEnabled = request.getRoutineEnabled();
        if (request.getRoutineReportEnabled() != null) this.routineReportEnabled = request.getRoutineReportEnabled();
        if (request.getCommunityEnabled() != null) this.communityEnabled = request.getCommunityEnabled();
        if (request.getCommunityHotEnabled() != null) this.communityHotEnabled = request.getCommunityHotEnabled();
        if (request.getCommentEnabled() != null) this.commentEnabled = request.getCommentEnabled();
        if (request.getMarketingEnabled() != null) this.marketingEnabled = request.getMarketingEnabled();
    }
}