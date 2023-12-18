package com.example.foodking.Exception;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {

    LOGIN_FAIL(HttpStatus.BAD_REQUEST,"로그인에 실패하였습니다.");

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
