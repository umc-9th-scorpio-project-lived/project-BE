package com.lived.domain.member.converter;

import com.lived.domain.concern.entity.Concern;
import com.lived.domain.concern.entity.mapping.MemberConcern;
import com.lived.domain.member.dto.MemberRequestDTO;
import com.lived.domain.member.dto.MemberResponseDTO;
import com.lived.domain.member.entity.Member;
import com.lived.domain.member.enums.MemberStatus;

import java.time.LocalDateTime;


public class MemberConverter {

    // DTO -> Member 엔티티 변환
    public static Member toMember(MemberRequestDTO.SignUpDTO request, String nickname) {
        return Member.builder()
                .socialId(request.getSocialId()) // 소셜id
                .provider(request.getProvider()) // 소셜 제공자
                .name(request.getName())         // 이름
                .email(request.getEmail())       // 이메일
                .nickname(nickname)              // 닉네임
                .gender(request.getGender())     // 성별
                .birth(request.getBirth())       // 생년월일
                .livingPeriod(request.getLivingPeriod()) // 자취연차
                .notificationStatus(request.getNotificationStatus()) // 알림 수신 동의 여부 1: 수락, 2: 나중에
                .agreementDate(LocalDateTime.now()) // 알림 상태 설정 시점
                .status(MemberStatus.ACTIVE)     // 기본 상태값 설정
                .build();
    }

    // Member와 Concern을 매핑 엔티티로 변환
    public static MemberConcern toMemberConcern(Member member, Concern concern) {
        return MemberConcern.builder()
                .member(member)// memberid
                .concern(concern)//concernid
                .build();
    }

    // Entity -> Response DTO 변환 (결과 반환용)
    public static MemberResponseDTO.SignUpResultDTO toSignUpResultDTO(Member member, String accessToken, String refreshToken) {
        return MemberResponseDTO.SignUpResultDTO.builder()
                .memberId(member.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // AccessToken 재발급
    public static MemberResponseDTO.ReissueResultDTO toReissueResultDTO(String accessToken) {
        return MemberResponseDTO.ReissueResultDTO.builder()
                .accessToken(accessToken)
                .build();
    }
}