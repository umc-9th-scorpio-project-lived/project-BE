package com.lived.domain.notification.entity;

import com.lived.domain.member.entity.Member;
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
}