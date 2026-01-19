package com.lived.domain.post.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostCategory {

  SELF_LIFE("자취 일상"),
  COUNSEL("고민 상담소"),
  RECOMMEND("추천템"),
  TIP("자취 꿀팁");

  private final String label;
}