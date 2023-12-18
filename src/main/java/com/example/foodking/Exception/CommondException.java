package com.example.foodking.Exception;

import lombok.Getter;

@Getter
public class CommondException extends RuntimeException {
    private final ExceptionCode exceptionCode;

    public CommondException(ExceptionCode exceptionCode) {
        this.exceptionCode = exceptionCode;
    }
}
