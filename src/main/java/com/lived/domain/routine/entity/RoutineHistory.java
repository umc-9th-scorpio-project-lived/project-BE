package com.lived.domain.routine.entity;

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
@Table(name = "routine_history")
public class RoutineHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routine_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_routine_id", nullable = false)
    private MemberRoutine memberRoutine;

    @Column(name = "check_date", nullable = false)
    private LocalDate checkDate;

    @Builder.Default
    @Column(name = "is_done", nullable = false)
    private Boolean isDone = false;

    public void toggleDone() {
        this.isDone = !this.isDone;
    }
}
