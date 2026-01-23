package com.lived.domain.member.converter;

import com.lived.domain.member.dto.MemberBlockResponseDTO;
import com.lived.domain.member.entity.Block;

public class MemberBlockConverter {

  // MemberBlock Entity → BlockMemberResponse
  public static MemberBlockResponseDTO.BlockMemberResponse toBlockMemberResponse(
      Block memberBlock) {
    return MemberBlockResponseDTO.BlockMemberResponse.builder()
        .blockId(memberBlock.getId())
        .build();
  }
}