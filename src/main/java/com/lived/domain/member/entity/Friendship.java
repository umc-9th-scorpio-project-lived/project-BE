package com.lived.domain.member.entity;

import com.lived.domain.member.enums.FriendshipStatus;
import com.lived.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "friendship")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Friendship extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private Member requester; // 친구 신청을 보낸 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver; // 친구 신청을 받은 사람

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FriendshipStatus status; // PENDING, ACCEPTED, REJECTED

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false; // 삭제 여부
}
