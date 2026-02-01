package com.lived.domain.routine.entity.mapping;

import com.lived.domain.member.entity.Member;
import com.lived.domain.routine.dto.RoutineUpdateRequestDTO;
import com.lived.domain.routine.entity.Routine;
import com.lived.domain.routine.entity.enums.RepeatType;
import com.lived.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
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
    @Column(nullable = false, length = 20)
    private String emoji = "👍";

    @Builder.Default
    @Column(name = "is_alarm_on")
    private Boolean isAlarmOn = false;

    @Column(name = "alarm_time")
    private LocalTime alarmTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type", nullable = false, length = 20)
    private RepeatType repeatType;

    @Column(name = "repeat_value", length = 100)
    private String repeatValue;

    @Builder.Default
    @Column(name = "repeat_interval")
    private Integer repeatInterval = 1;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "routine_exclusion", joinColumns = @JoinColumn(name = "member_routine_id"))
    @Column(name = "excluded_date")
    private List<LocalDate> excludedDates = new ArrayList<>();


    public boolean isScheduledFor(LocalDate date) {
        if(date.isBefore((this.startDate)) || !this.isActive) return false;
        if(this.endDate != null && date.isAfter(this.endDate)) return false;

        if(this.excludedDates.contains(date)) return false;

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

        if(!activeDays.contains(todayNum)) return false;

        LocalDate startMonday = this.startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate targetMonday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        long weeksBetween = ChronoUnit.WEEKS.between(startMonday, targetMonday);

        return (weeksBetween % this.repeatInterval) == 0;
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

    public void update(RoutineUpdateRequestDTO request) {
        this.title = request.title();
        this.emoji = request.emoji();
        this.repeatType = request.repeatType();
        this.repeatInterval = request.repeatInterval() != null ? request.repeatInterval() : 1;
        this.repeatValue = request.getJoinedRepeatValue();
        this.alarmTime = request.alarmTime();
        this.isAlarmOn = request.isAlarmOn();
    }

    // 이 일정만 삭제
    public void excludeDate(LocalDate date) {
        if(!this.excludedDates.contains(date)) {
            this.excludedDates.add(date);
        }
    }

    // 이후 일정 삭제
    public void terminateAt(LocalDate date) {
        this.endDate = date.minusDays(1);
        if(this.endDate.isBefore(this.startDate)) {
            this.isActive = false;
        }
    }
}
