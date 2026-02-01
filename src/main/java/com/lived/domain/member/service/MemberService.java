package com.lived.domain.member.service;


import com.lived.domain.concern.entity.mapping.MemberConcern;
import com.lived.domain.concern.repository.ConcernRepository;
import com.lived.domain.concern.repository.MemberConcernRepository;
import com.lived.domain.member.converter.MemberConverter;
import com.lived.domain.member.dto.MemberRequestDTO;
import com.lived.domain.member.dto.MemberResponseDTO;
import com.lived.domain.member.entity.Member;
import com.lived.domain.member.entity.NicknameWord;
import com.lived.domain.member.enums.MemberStatus;
import com.lived.domain.member.enums.Provider;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.member.repository.NicknameWordRepository;
import com.lived.domain.routine.entity.enums.RepeatType;
import com.lived.domain.routine.entity.mapping.MemberRoutine;
import com.lived.domain.routine.repository.MemberRoutineRepository;
import com.lived.domain.routine.repository.RoutineRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import com.lived.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberConcernRepository memberConcernRepository;
    private final MemberRoutineRepository memberRoutineRepository;
    private final ConcernRepository concernRepository; // 고민 원본 데이터 조회용
    private final RoutineRepository routineRepository; // 추천 루틴 원본 데이터 조회용
    private final NicknameWordRepository wordRepository;

    // JWT Refresh 토큰 갱신 로직
    public void updateRefreshToken(String socialId, Provider provider, String refreshToken) {
        Member member = memberRepository.findBySocialIdAndProvider(socialId, provider)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_SOCIAL_NOT_FOUND));

        member.updateRefreshToken(refreshToken); // Member 엔티티에 추가한 메서드
    }

    // JWT Acceess 토큰 갱신 로직
    public MemberResponseDTO.ReissueResultDTO reissueAccessToken(Long memberId, String refreshToken) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        //DB에 저장된 리프레시 토큰과 클라이언트가 보낸 토큰이 일치하는지 검증
        if (!refreshToken.equals(member.getRefreshToken())) {
            throw new GeneralException(GeneralErrorCode.REFRESH_TOKEN_NOT_MATCH);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(memberId, member.getProvider().toString());

        //새로운 토큰 생성하여 반환
        return MemberConverter.toReissueResultDTO(newAccessToken);
    }

    // 회원 가입 로직
    public MemberResponseDTO.SignUpResultDTO signup(MemberRequestDTO.SignUpDTO request) {

        // 중복 가입 확인
        if (memberRepository.existsBySocialIdAndProvider(request.getSocialId(), request.getProvider())) {
            throw new GeneralException(GeneralErrorCode.MEMBER_ALREADY_EXISTS);
        }

        // 닉네임 생성
        String nickname = generateRandomNickname();

        // Member 저장
        Member member = memberRepository.save(MemberConverter.toMember(request, nickname));

        // MemberConcern 저장
        if (request.getConcernIds() != null) {
            List<MemberConcern> concerns = request.getConcernIds().stream()
                    .map(id -> concernRepository.findById(id).orElseThrow())
                    .map(c -> MemberConverter.toMemberConcern(member, c))
                    .collect(Collectors.toList());
            memberConcernRepository.saveAll(concerns);
        }

        // MemberRoutine 저장
        if (request.getRoutineIds() != null) {
            List<MemberRoutine> routines = request.getRoutineIds().stream()
                    .map(id -> routineRepository.findById(id).orElseThrow())
                    .map(r -> MemberRoutine.builder()
                            .member(member) // memberId
                            .routine(r) // routineId
                            .title(r.getTitle())    // 루틴 이름
                            .emoji(r.getCategory().getEmoji())  // 이모지 복사 저장
                            .repeatType(RepeatType.WEEKLY)
                            .repeatValue("0,1,2,3,4,5,6")
                            .isActive(true) // 기본값 true
                            .isAlarmOn(false)   // 기본값 false
                            .startDate(LocalDate.now()) // 시작일은 당일로 설정
                            .build())
                    .collect(Collectors.toList());
            memberRoutineRepository.saveAll(routines);
        }

        // JWT 토큰 생성 및 저장 (자동 로그인 로직)
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getProvider().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        member.updateRefreshToken(refreshToken);

        // 결과 반환
        return MemberConverter.toSignUpResultDTO(
                member,
                accessToken,
                refreshToken
        );
    }

    // 닉네임 생성 로직
    private String generateRandomNickname() {
        String nickname;

        do {
            // 형용사 타입에서 서로 다른 2개 추출
            List<NicknameWord> adjs = wordRepository.findRandomByType("ADJECTIVE", 2);
            // 명사 타입에서 1개 추출
            List<NicknameWord> nouns = wordRepository.findRandomByType("NOUN", 1);

            if (adjs.size() < 2 || nouns.isEmpty()) {
                throw new GeneralException(GeneralErrorCode.NICKNAME_GENERATE_FAILED);
            };

            // 결과
            nickname = adjs.get(0).getWord() + " " + adjs.get(1).getWord() + " " + nouns.get(0).getWord();

        } while (memberRepository.existsByNickname(nickname)); // 최종 결과 중복 체크
        return nickname;
    }

    // 로그아웃 로직
    @Transactional
    public void logout(Long memberId, HttpServletResponse response) {
        // 사용자 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        // 리프레시 토큰 제거
        member.logout();

        // 브라우저 쿠키 무효화
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    // 회원탈퇴 로직
    @Transactional
    public void withdraw(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));
        member.withdraw();
    }

    // 탈퇴 30일 후 스케줄러
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupInactiveMembers() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        // 대상 조회
        List<Member> targets = memberRepository.findAllByStatusAndInactiveDateBefore(MemberStatus.INACTIVE, threshold);
        // 30일 지난 계정 삭제
        if (!targets.isEmpty()) {
            targets.forEach(Member::anonymize);
        }
    }
}


