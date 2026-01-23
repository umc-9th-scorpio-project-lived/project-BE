package com.lived.domain.member.service;

import com.lived.domain.member.converter.MemberBlockConverter;
import com.lived.domain.member.dto.MemberBlockRequestDTO;
import com.lived.domain.member.dto.MemberBlockResponseDTO;
import com.lived.domain.member.entity.Block;
import com.lived.domain.member.entity.Member;
import com.lived.domain.member.repository.MemberBlockRepository;
import com.lived.domain.member.repository.MemberRepository;
import com.lived.global.apiPayload.code.GeneralErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberBlockService {

  private final MemberBlockRepository memberBlockRepository;
  private final MemberRepository memberRepository;

  /**
   * 사용자 차단
   */
  @Transactional
  public MemberBlockResponseDTO.BlockMemberResponse blockMember(
      Long blockerId,
      MemberBlockRequestDTO.BlockMemberRequest request
  ) {
    // 자기 자신 차단 방지
    if (blockerId.equals(request.getBlockedMemberId())) {
      throw new GeneralException(GeneralErrorCode.BLOCK_SELF_NOT_ALLOWED);
    }

    // Blocker 조회
    Member blocker = memberRepository.findById(blockerId)
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

    // Blocked 조회
    Member blocked = memberRepository.findById(request.getBlockedMemberId())
        .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

    // 중복 차단 확인
    boolean alreadyBlocked = memberBlockRepository.existsByBlockerIdAndBlockedId(
        blockerId, request.getBlockedMemberId()
    );
    if (alreadyBlocked) {
      throw new GeneralException(GeneralErrorCode.BLOCK_ALREADY_EXISTS);
    }

    // MemberBlock 생성
    Block memberBlock = Block.builder()
        .blocker(blocker)
        .blocked(blocked)
        .build();
    Block savedBlock = memberBlockRepository.save(memberBlock);

    return MemberBlockConverter.toBlockMemberResponse(savedBlock);
  }
}