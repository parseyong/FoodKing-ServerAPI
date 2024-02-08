package com.example.foodking.exception.handler;

import com.example.foodking.common.CommonResDTO;
import com.example.foodking.exception.CommondException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResDTO.of("올바르지 않은 입력값입니다",errors));
    }

    // requestParam으로 입력받은 값의 유효성검사 실패 시
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResDTO> handleContranintViolation(ConstraintViolationException ex){
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            if(fieldName.contains("email"))
                fieldName="email";
            else if(fieldName.contains("phoneNum"))
                fieldName="phoneNum";
            else if(fieldName.contains("nickName"))
                fieldName="nickName";
            else if(fieldName.contains("recipeInfoId"))
                fieldName="recipeInfoId";
            else if(fieldName.contains("recipeImage"))
                fieldName="recipeImage";
            else if(fieldName.contains("content"))
                fieldName="content";

            String message = violation.getMessage();
            errors.put(fieldName,message);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResDTO.of("올바르지 않은 입력값입니다",errors));
    }

    /*
        RequestParam으로 받은 값이 존재하지 않을때 발생하는 예외처리 로직
        이 예외는 사용자의 잘못된 요청값이 아닌 클라이언트 개발자가 폼데이터를 통해 요청을 보내지 않았을 때 발생한다.
        ex) 폼데이터가 아닌 json형태로 요청을 보냈을 경우
    */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getParameterName(), ex.getMessage()+"(관리자에게 문의하세요)");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommonResDTO.of("올바르지 않은 입력값입니다",errors));
    }
    /*
        Multipart 파일입력 시 Multipart로 받은 값이 존재하지 않을때 발생하는 예외처리 로직
        클라이언트 개발자가 폼데이터를통해 파일입력요청을 보내지 않았을 때 발생
    */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<CommonResDTO> handleMultipartException(MultipartException ex){
        Map<String, String> errors = new HashMap<>();
        errors.put("recipeImage","파일이 존재하지 않습니다. 관리자에게 문의하세요");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommonResDTO.of("올바르지 않은 입력값입니다",errors));
    }

    // PathVariable로 입력받은 값이 공백일 경우 발생하는 예외처리
    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(
            MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonResDTO.of("올바른 요청이 아닙니다.",null));
    }

    // 커스텀 예외발생 시
    @ExceptionHandler(CommondException.class)
    public ResponseEntity<CommonResDTO> handleCommandException(CommondException ex){
        Map<String, String> errors = new HashMap<>();
        String fieldName = ex.getExceptionCode().getFieldName();
        if(fieldName != null)
            errors.put(fieldName,ex.getExceptionCode().getMessage());

        log.error("예외가 발생했습니다. - "+fieldName+":"+ex.getExceptionCode().getMessage());
        return ResponseEntity
                .status(ex.getExceptionCode().getStatus())
                .body(CommonResDTO.of(ex.getExceptionCode().getMessage(),errors));
    }
    
    // 역직렬화 과정에서 dto필드의 타입이 맞지 않아 발생하는 예외
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        String errorMessage = ex.getMessage();
        if(errorMessage.contains("java.lang.Long"))
            errorMessage = ": Long타입 예외";
        else if(errorMessage.contains("Enum"))
            errorMessage = ": ENUM타입 예외";

        String message = "올바른 요청타입이 아닙니다. 관리자에게 문의하세요";
        log.error(message+errorMessage+":"+ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResDTO.of(message+errorMessage,null));
    }

    // @PathVariable로 입력받은 값의 타입이 올바르지 않을 때
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CommonResDTO> handleMethodArgTypeException(MethodArgumentTypeMismatchException ex){
        Map<String ,String> map= new HashMap<>();

        String fieldName = ex.getName();
        String requiredType = ex.getRequiredType().getSimpleName();
        String message = fieldName+"이 "+requiredType+"타입이여야 합니다.";
        map.put("fieldName",fieldName);
        map.put("requiredType",requiredType);
        log.error("URI값이 올바르지 않습니다. - "+ map.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResDTO.of(message,map));
    }

    @ExceptionHandler
    public ResponseEntity<CommonResDTO> handleException(Exception ex) {

        String message = "서버 내부에 에러가 발생했습니다.";
        log.error(message+":"+ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommonResDTO.of(message,null));
    }

    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String cause = ex.getMessage();
        if(cause.contains("DateTimeParseException")){
            String message = "올바른 날짜형식이 아닙니다";
            log.error(message+":"+ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResDTO.of(message,null));
        }

        return ResponseEntity.status(status).body(CommonResDTO.of("원인을 알 수 없는 예외가 발생했습니다.",cause));
    }

    //요청 메소드가 올바르지 않을 때
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = "올바른 요청이 아닙니다.";
        log.error(message+":"+ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(CommonResDTO.of(message,null));
    }
}


