package com.lived.domain.member.service;

import com.lived.domain.concern.entity.Concern;
import com.lived.domain.concern.entity.mapping.MemberConcern;
import com.lived.domain.concern.repository.ConcernRepository;
import com.lived.domain.concern.repository.MemberConcernRepository;
import com.lived.domain.member.converter.MemberConverter;
import com.lived.domain.member.dto.MemberRequestDTO;
import com.lived.domain.member.dto.MemberResponseDTO;
import com.lived.domain.member.entity.Member;
import com.lived.domain.member.entity.NicknameWord;
import com.lived.domain.member.enums.Provider;
import com.lived.domain.member.enums.WordType;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.domain.member.repository.NicknameWordRepository;
import com.lived.domain.routine.entity.Routine;
import com.lived.domain.routine.entity.mapping.MemberRoutine;
import com.lived.domain.routine.repository.MemberRoutineRepository;
import com.lived.domain.routine.repository.RoutineRepository;
import com.lived.global.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
                .orElseThrow(() -> new RuntimeException("해당 소셜 계정으로 가입된 유저가 없습니다."));

        member.updateRefreshToken(refreshToken); // Member 엔티티에 추가한 메서드
    }

    // JWT Acceess 토큰 갱신 로직
    public String reissueAccessToken(Long memberId, String oldRefreshToken) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        //DB에 저장된 리프레시 토큰과 클라이언트가 보낸 토큰이 일치하는지 검증
        if (!oldRefreshToken.equals(member.getRefreshToken())) {
            throw new RuntimeException("리프레시 토큰이 일치하지 않습니다. 다시 로그인하세요.");
        }

        //새로운 토큰 생성하여 반환
        return jwtTokenProvider.createAccessToken(member.getId(), member.getProvider().name());
    }

    // 회원 가입 로직
    public MemberResponseDTO.SignUpResultDTO SignUp(MemberRequestDTO.SignUpDTO request) {

        // 중복 가입 확인
        if (memberRepository.existsBySocialIdAndProvider(request.getSocialId(), request.getProvider())) {
            throw new RuntimeException("이미 가입된 회원입니다.");
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
                throw new RuntimeException("닉네임 생성에 필요한 단어가 부족합니다.");
            };

            // 결과
            nickname = adjs.get(0).getWord() + " " + adjs.get(1).getWord() + " " + nouns.get(0).getWord();

        } while (memberRepository.existsByNickname(nickname)); // 최종 결과 중복 체크
        return nickname;
    }
}


