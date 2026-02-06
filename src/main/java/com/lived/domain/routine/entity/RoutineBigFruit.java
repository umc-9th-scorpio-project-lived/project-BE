package com.lived.domain.routine.entity;

import com.lived.domain.member.entity.Member;
import com.lived.domain.routine.enums.BigFruitType;
import com.lived.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RoutineBigFruit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private BigFruitType type;

    private int currentValue;

    private int goalValue;
}
