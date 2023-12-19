package com.example.foodking.Exception.Handler;

import com.example.foodking.Common.CommonResDTO;
import com.example.foodking.Exception.CommondException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Log4j2
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {

    //@Valid 유효성검사 실패 시
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        log.error(errors.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResDTO.of("BAD_REQUEST","올바르지 않은 입력값입니다",errors));
    }

    // 커스텀 예외발생 시
    @ExceptionHandler(CommondException.class)
    public ResponseEntity<CommonResDTO> commandExceptionHandler(CommondException ex){
        log.error("예외가 발생했습니다. - "+ex.getExceptionCode().getMessage());
        return ResponseEntity
                .status(ex.getExceptionCode().getStatus())
                .body(CommonResDTO.of(ex.getExceptionCode().name(),ex.getExceptionCode().getMessage(),null));

    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<CommonResDTO> handleException(Exception ex) {
        String message = "서버 내부에 에러가 발생했습니다.";
        log.error(message+":"+ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommonResDTO.of("INTERNAL_SERVER_ERROR",message,null));
    }

    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String cause = ex.getMessage();
        if(cause.contains("DateTimeParseException")){
            String message = "올바른 날짜형식이 아닙니다";
            log.error(message+":"+ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResDTO.of("BAD_REQUEST",message,null));
        }

        return ResponseEntity.status(status).body(CommonResDTO.of(status.toString(),"원인을 알 수 없는 예외가 발생했습니다.",cause));
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = "올바른 요청이 아닙니다.";
        log.error(message+":"+ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(CommonResDTO.of("METHOD_NOT_ALLOWED",message,null));
    }
}


