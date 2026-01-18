package com.lived.domain.routine.entity.mapping;

import com.lived.domain.member.entity.Member;
import com.lived.domain.routine.entity.Routine;
import com.lived.domain.routine.entity.enums.RepeatType;
import com.lived.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "member_routine")
public class MemberRoutine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_routine_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @Column(name = "title", nullable = false)
    private String title;

    @Builder.Default
    @Column(name = "is_alarm_on")
    private Boolean isAlarmOn = false;

    @Column(name = "alarm_time")
    private LocalTime alarmTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type", length = 20)
    private RepeatType repeatType;

    @Column(name = "repeat_value", length = 100)
    private String repeatValue;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String emoji = "👍";

    public boolean isScheduledFor(LocalDate date) {
        if(date.isBefore((this.startDate)) || !this.isActive) return false;

        return switch (this.repeatType) {
            case WEEKLY -> checkWeekly(date);
            case MONTHLY -> checkMonthly(date);
        };
    }

    /**
     * 주 단위 반복 체크 (일: 0, 월: 1, ..., 토:6)
     */
    private boolean checkWeekly(LocalDate date) {
        int dayValue = date.getDayOfWeek().getValue() % 7;

        String todayNum = String.valueOf(dayValue);
        List<String> activeDays = Arrays.asList(this.repeatValue.split(","));

        return activeDays.contains(todayNum);
    }

    /**
     * 월 단위 반복 체크: "1, 15, L" (1일, 15일, 마지막 날)
     */
    private boolean checkMonthly(LocalDate date) {
        List<String> targetDays = Arrays.asList(this.repeatValue.split(","));

        // 오늘의 날짜(일)
        String todayDayOfMonth = String.valueOf(date.getDayOfMonth());

        boolean isSpecificDayMatch = targetDays.contains(todayDayOfMonth);

        boolean isLastDayMatch = targetDays.contains("L") && (date.getDayOfMonth() == date.lengthOfMonth());

        return isSpecificDayMatch || isLastDayMatch;
    }
}
