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
    @Column(name = "community_enabled", nullable = false)
    private Boolean communityEnabled = true;

    @Builder.Default
    @Column(name = "post_like_enabled", nullable = false)
    private Boolean postLikeEnabled = true;

    @Builder.Default
    @Column(name = "comment_enabled", nullable = false)
    private Boolean commentEnabled = true;

    @Builder.Default
    @Column(name = "comment_like_enabled", nullable = false)
    private Boolean commentLikeEnabled = true;

    @Builder.Default
    @Column(name = "marketing_enabled", nullable = false)
    private Boolean marketingEnabled = false;

    public void update(NotificationRequestDTO.NotificationSettingDTO request) {
        // 전체 알림 설정(allEnabled)이 들어왔는지 확인
        if (request.getAllEnabled() != null) {
            this.allEnabled = request.getAllEnabled();

            // 전체 설정이 바뀌면 하위 항목들도 모두 동일한 값으로 강제 변경
            this.routineEnabled = request.getAllEnabled();
            this.communityEnabled = request.getAllEnabled();
            this.postLikeEnabled = request.getAllEnabled();
            this.commentEnabled = request.getAllEnabled();
            this.commentLikeEnabled = request.getAllEnabled();
            this.marketingEnabled = request.getAllEnabled();

            // 전체 설정을 건드렸을 때는 하위 개별 설정 로직을 타지 않고 바로 종료
            return;
        }

        // 전체 설정이 들어오지 않은 경우에만 각자 개별적으로 업데이트
        if (request.getRoutineEnabled() != null) this.routineEnabled = request.getRoutineEnabled();
        if (request.getCommunityEnabled() != null) this.communityEnabled = request.getCommunityEnabled();
        if (request.getPostLikeEnabled() != null) this.postLikeEnabled = request.getPostLikeEnabled();
        if (request.getCommentEnabled() != null) this.commentEnabled = request.getCommentEnabled();
        if (request.getCommentLikeEnabled() != null) this.commentLikeEnabled = request.getCommentLikeEnabled();
        if (request.getMarketingEnabled() != null) this.marketingEnabled = request.getMarketingEnabled();
    }
}