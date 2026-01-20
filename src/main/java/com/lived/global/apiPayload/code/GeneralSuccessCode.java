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
    CREATED(HttpStatus.CREATED, "COMMON201_1", "리소스 생성이 완료되었습니다."),

    // 루틴 관련 성공 코드
    ROUTINE_CREATED(HttpStatus.CREATED, "ROUTINE201_1", "루틴 생성 성공입니다."),
    ROUTINE_OK(HttpStatus.OK, "ROUTINE200_1", "루틴 조회 성공입니다."),
    ROUTINE_UPDATED(HttpStatus.OK, "ROUTINE200_2", "루틴 수정 성공입니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
