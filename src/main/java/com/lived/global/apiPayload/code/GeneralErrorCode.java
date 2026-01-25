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
    MEMBER_SOCIAL_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_2", "해당 소셜 계정으로 가입된 유저가 없습니다."),
    REFRESH_TOKEN_NOT_MATCH(HttpStatus.BAD_REQUEST, "AUTH400_2", "리프레시 토큰이 일치하지 않습니다. 다시 로그인하세요."),
    MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER409_1", "이미 가입된 회원입니다."),
    NICKNAME_GENERATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER500_1", "닉네임 생성에 필요한 단어가 부족합니다."),

    // 루틴 관련 에러 코드
    ROUTINE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROUTINE404_1", "해당 루틴을 찾을 수 없습니다."),
    ROUTINE_ALREADY_EXISTS(HttpStatus.CONFLICT, "ROUTINE409_1", "이미 존재하는 루틴 이름입니다."),

    // 커뮤니티 관련 에러 코드
    POST_IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "COMMUNITY400_1", "이미지는 최대 10개까지 업로드 가능합니다."),
    COMMENT_NOT_MATCH_POST(HttpStatus.BAD_REQUEST, "COMMENT400_2", "해당 게시글의 댓글이 아닙니다."),
    POST_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMUNITY403_1", "게시글에 대한 권한이 없습니다."),
    COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMUNITY403_2", "댓글에 대한 권한이 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMUNITY404_1", "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMUNITY404_2", "댓글을 찾을 수 없습니다."),
    REPORT_TARGET_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMUNITY404_3", "신고 대상을 찾을 수 없습니다."),
    REPORT_ALREADY_EXISTS(HttpStatus.CONFLICT, "COMMUNITY409_1", "신고가 이미 접수되었습니다."),

    // 사용자 차단 관련 에러 코드
    BLOCK_ALREADY_EXISTS(HttpStatus.CONFLICT, "BLOCK409_1", "이미 차단한 사용자입니다."),
    BLOCK_SELF_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "BLOCK400_1", "자기 자신을 차단할 수 없습니다."),
    BLOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "BLOCK404_1", "차단 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
