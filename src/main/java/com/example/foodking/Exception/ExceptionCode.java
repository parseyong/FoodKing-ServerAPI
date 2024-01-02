package com.example.foodking.Exception;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {

    LOGIN_FAIL(HttpStatus.BAD_REQUEST,"로그인에 실패하였습니다.",null),
    SMS_AUTHENTICATION_FAIL(HttpStatus.BAD_REQUEST,"인증번호가 올바르지 않습니다","authenticationNumber"),
    SMS_NOT_AUTHENTICATION(HttpStatus.BAD_REQUEST,"인증이 되지않은 번호입니다.","phoneNum"),
    COOLSMS_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"인증시스템에 문제가 발생했습니다",null),
    EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST,"중복된 이메일입니다","email"),
    NICKNAME_DUPLICATED(HttpStatus.BAD_REQUEST,"중복된 닉네임입니다","nickName"),
    PASSWORD_NOT_COLLECT(HttpStatus.BAD_REQUEST,"비밀번호가 일치하지 않습니다.","password"),
    NOT_PHONENUM(HttpStatus.BAD_REQUEST,"올바른 전화번호 형식이 아닙니다","phoneNum"),
    NOT_EXIST_USER(HttpStatus.BAD_REQUEST,"존재하지 않는 유저입니다",null),
    NOT_EXIST_RECIPEINFO(HttpStatus.BAD_REQUEST,"존재하지 않는 레시피입니다","recipeInfoId"),
    INVALID_SAVE_FILE(HttpStatus.BAD_REQUEST,"등록할 파일이 존재하지 않습니다. 파일을 추가해주세요.","recipeImage"),
    FILE_IOEXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"파일 저장중 문제가 발생했습니다.","recipeImage");

    private final HttpStatus status;
    private final String field;
    private final String message;
    ExceptionCode(HttpStatus status, String message,String field) {
        this.status = status;
        this.message = message;
        this.field=field;
    }

    public HttpStatus getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }
    public String getFieldName() {return field; }
}
