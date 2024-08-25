package com.example.foodking.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {

    // USER EXCEPTION
    NOT_EXIST_USER(HttpStatus.BAD_REQUEST,"존재하지 않는 유저입니다",null),
    EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST,"중복된 이메일입니다","email"),
    NICKNAME_DUPLICATED(HttpStatus.BAD_REQUEST,"중복된 닉네임입니다","nickName"),
    PASSWORD_NOT_COLLECT(HttpStatus.BAD_REQUEST,"비밀번호가 일치하지 않습니다.","password"),
    ACCESS_FAIL_USER(HttpStatus.FORBIDDEN,"해당 유저에 대한 권한이 없습니다",null),
    PHONE_NUMBER_DUPLICATED(HttpStatus.BAD_REQUEST,"해당 번호로 이미 가입된 계정이 있습니다.","phoneNum"),

    // Login, CoolSMS EXCEPTION
    LOGIN_FAIL(HttpStatus.BAD_REQUEST,"로그인에 실패하였습니다.",null),
    SMS_AUTHENTICATION_FAIL(HttpStatus.BAD_REQUEST,"인증번호가 올바르지 않습니다","authenticationNumber"),
    SMS_NOT_AUTHENTICATION(HttpStatus.BAD_REQUEST,"인증이 되지않은 번호입니다.","phoneNum"),
    NOT_PHONENUM_TYPE(HttpStatus.BAD_REQUEST,"올바른 전화번호 형식이 아닙니다","phoneNum"),
    COOLSMS_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"인증시스템에 문제가 발생했습니다",null),

    // RECIPE EXCEPTION
    NOT_EXIST_RECIPEINFO(HttpStatus.BAD_REQUEST,"존재하지 않는 레시피입니다",null),
    ACCESS_FAIL_RECIPE(HttpStatus.FORBIDDEN,"해당 레시피에 대해 권한이 없습니다",null),
    NOT_EXIST_PAGE(HttpStatus.NOT_FOUND,"존재하지 않는 페이지입니다.",null),

    // FILE EXCEPTION
    INVALID_SAVE_FILE(HttpStatus.BAD_REQUEST,"등록할 파일이 존재하지 않습니다. 파일을 추가해주세요.","recipeImage"),
    NOT_EXIST_FILE(HttpStatus.BAD_REQUEST,"파일이 존재하지 않습니다",null),
    FILE_IOEXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"파일 저장중 문제가 발생했습니다.","recipeImage"),
    ACCESS_FAIL_FILE(HttpStatus.FORBIDDEN,"해당 파일에 대한 권한이 없습니다",null),

    //REPLY EXCEPTION
    NOT_EXIST_REPLY(HttpStatus.BAD_REQUEST,"존재하지 않는 댓글입니다",null),
    ACCESS_FAIL_REPLY(HttpStatus.FORBIDDEN,"해당 댓글에 대한 권한이 없습니다",null),

    //EMOTION EXCEPTION
    ACCESS_FAIL_EMOTION(HttpStatus.FORBIDDEN,"해당 이모션에 대한 권한이 없습니다",null),

    //Lock Exception
    LOCK_CAPTURE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR,"서버에 요청이 많아 데이터반환에 실패했습니다. 다시 요청해주세요",null);

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
