package com.lived.domain.routine.entity;

import com.lived.domain.routine.enums.FruitType;
import com.lived.domain.routine.entity.mapping.MemberRoutine;
import com.lived.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "routine_fruit")
public class RoutineFruit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routine_fruit_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_routine_id", nullable = false)
    private MemberRoutine memberRoutine;

    @Column(name = "month", nullable = false)
    private LocalDate month;

    @Builder.Default
    @Column(name = "achievement_rate", nullable = false)
    private Double achievementRate = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "fruit_type", nullable = false)
    private FruitType fruitType;

    public void updateAchievement(Double newRate) {
        this.achievementRate = newRate;
        this.fruitType = calculateFruitType(newRate);
    }

    private FruitType calculateFruitType(Double rate) {
        if (rate >= 90.0){
            return FruitType.GOLD;
        } else if (rate >= 60.0) {
            return FruitType.NORMAL;
        } else if (rate >= 30.0) {
            return FruitType.GROWING;
        }
        return FruitType.NONE;
    }
}
