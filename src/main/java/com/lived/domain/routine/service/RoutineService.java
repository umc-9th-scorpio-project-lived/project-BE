package com.lived.domain.routine.service;

import com.lived.domain.member.entity.Member;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.routine.dto.RoutineRequestDTO;
import com.lived.domain.routine.entity.mapping.MemberRoutine;
import com.lived.domain.routine.repository.MemberRoutineRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class RoutineService {

    private final MemberRoutineRepository memberRoutineRepository;
    private final MemberRepository memberRepository;

    public Long createCustomRoutine(Long memberId, RoutineRequestDTO request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        MemberRoutine newRoutine = MemberRoutine.builder()
                .member(member)
                .title(request.getTitle())
                .emoji(request.getEmoji())
                .repeatType(request.getRepeatType())
                .repeatInterval(request.getRepeatInterval())
                .repeatValue(request.getRepeatValueAsString())
                .isAlarmOn(request.getIsAlarmon())
                .alarmTime(request.getIsAlarmon() ? request.getAlarmTime() : null)
                .startDate(LocalDate.now())
                .isActive(true)
                .build();

        return memberRoutineRepository.save(newRoutine).getId();
    }

}
