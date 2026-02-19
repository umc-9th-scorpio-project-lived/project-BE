package com.lived.domain.member.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatus {
    ACTIVE("활성 상태"),
    INACTIVE("탈퇴 유예 상태"),
    DELETED("완전 삭제 상태");

    private final String description;
}