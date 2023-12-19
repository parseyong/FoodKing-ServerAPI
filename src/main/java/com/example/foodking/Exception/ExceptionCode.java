package com.example.foodking.Exception;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {

    LOGIN_FAIL(HttpStatus.BAD_REQUEST,"로그인에 실패하였습니다."),
    SMS_AUTHENTICATION_FAIL(HttpStatus.BAD_REQUEST,"인증번호가 올바르지 않습니다"),
    SMS_NOT_AUTHENTICATION(HttpStatus.BAD_REQUEST,"인증이 되지않은 번호입니다."),
    COOLSMS_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"인증시스템에 문제가 발생했습니다"),
    EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST,"중복된 이메일입니다"),
    NICKNAME_DUPLICATED(HttpStatus.BAD_REQUEST,"중복된 닉네임입니다"),
    PASSWORD_NOT_COLLECT(HttpStatus.BAD_REQUEST,"비밀번호가 일치하지 않습니다.");

    private final HttpStatus status;
    private final String message;
    ExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }
}
