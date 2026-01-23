package com.lived.domain.member.converter;

import com.lived.domain.member.dto.MemberBlockResponseDTO;
import com.lived.domain.member.entity.Member;
import com.lived.domain.member.entity.Block;
import com.lived.global.dto.CursorPageResponse;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.stream.Collectors;

public class MemberBlockConverter {

  // Block Entity → BlockMemberResponse
  public static MemberBlockResponseDTO.BlockMemberResponse toBlockMemberResponse(Block block) {
    return MemberBlockResponseDTO.BlockMemberResponse.builder()
        .blockId(block.getId())
        .build();
  }

  // Block Entity → BlockedMemberInfo
  public static MemberBlockResponseDTO.BlockedMemberInfo toBlockedMemberInfo(Block block) {
    Member blocked = block.getBlocked();

    return MemberBlockResponseDTO.BlockedMemberInfo.builder()
        .blockId(block.getId())
        .memberId(blocked.getId())
        .nickname(blocked.getNickname())
        .profileImageUrl(blocked.getProfileImageUrl())
        .build();
  }

  // Slice<Block> → CursorPageResponse<BlockedMemberInfo>
  public static CursorPageResponse<MemberBlockResponseDTO.BlockedMemberInfo> toBlockListResponse(
      Slice<Block> blockSlice
  ) {
    List<MemberBlockResponseDTO.BlockedMemberInfo> blockedMembers = blockSlice.getContent().stream()
        .map(MemberBlockConverter::toBlockedMemberInfo)
        .collect(Collectors.toList());

    Long nextCursor = null;
    if (blockSlice.hasNext() && !blockSlice.getContent().isEmpty()) {
      nextCursor = blockSlice.getContent().get(blockSlice.getContent().size() - 1).getId();
    }

    return CursorPageResponse.<MemberBlockResponseDTO.BlockedMemberInfo>builder()
        .content(blockedMembers)
        .hasNext(blockSlice.hasNext())
        .nextCursor(nextCursor)
        .build();
  }
}