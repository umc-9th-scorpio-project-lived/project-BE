package com.lived.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralSuccessCode implements BaseSuccessCode {

    // 200 OK: 가장 일반적인 성공 응답
    OK(HttpStatus.OK, "COMMON200_1", "요청에 성공하였습니다."),

    // 201 CREATED: 새로운 리소스(루틴, 게시글 등) 생성 성공
    CREATED(HttpStatus.CREATED, "COMMON201_1", "리소스 생성이 완료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
