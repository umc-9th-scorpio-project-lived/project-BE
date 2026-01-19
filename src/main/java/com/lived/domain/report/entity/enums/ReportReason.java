package com.lived.domain.report.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportReason {

  ABUSE("욕설·혐오 표현"),
  ADULT("음란·부적절한 콘텐츠"),
  SPAM("상업성·홍보성 내용"),
  JUNK("도배·장난성 글"),
  PRIVACY("개인정보 침해"),
  COPYRIGHT("저작권 침해"),
  CRIME("범죄 행위 유도"),
  OTHER("기타");

  private final String description;
}