package com.lived.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralErrorCode implements BaseErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400_1", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH401_1", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH403_1", "요청이 거부되었습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404_1", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500_1", "예기치 않은 서버 에러가 발생했습니다."),

    // 회원 관련 에러 코드
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_1", "사용자를 찾을 수 없습니다."),

    // 루틴 관련 에러 코드
    ROUTINE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROUTINE404_1", "해당 루틴을 찾을 수 없습니다."),
    ROUTINE_ALREADY_EXISTS(HttpStatus.CONFLICT, "ROUTINE409_1", "이미 존재하는 루틴 이름입니다."),

    // 커뮤니티 관련 에러 코드
    POST_IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "COMMUNITY400_1", "이미지는 최대 10개까지 업로드 가능합니다."),
    COMMENT_NOT_MATCH_POST(HttpStatus.BAD_REQUEST, "COMMENT400_2", "해당 게시글의 댓글이 아닙니다."),
    POST_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMUNITY403_1", "게시글에 대한 권한이 없습니다."),
    COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMUNITY403_2", "댓글에 대한 권한이 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMUNITY404_1", "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT404_2", "댓글을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
