package com.green.eats.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
@ConditionalOnProperty(name = "constants.exception.common-handler.enabled", havingValue = "true")
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {

    // 비즈니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<Object> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getErrorCode().getMessage());
        return handleExceptionInternal(e.getErrorCode());
    }

    //Validation 예외가 발생되었을 경우 캐치
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex
            , HttpHeaders headers
            , HttpStatusCode statusCode
            , WebRequest request) {
        return handleExceptionInternal(CommonErrorCode.INVALID_INPUT_VALUE, ex);
    }

    // 시스템 예외 처리 (예: DB 연결 실패 등)
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleException(Exception e) {
        log.error("Exception: ", e);
        MyErrorResponse response = new MyErrorResponse(
                CommonErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return handleExceptionInternal(errorCode, null);
    }

    private static ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, BindException bindException) {
        List<MyErrorResponse.FieldError> fieldErrorList = null;
        if(bindException != null) {
            fieldErrorList = getValidationError(bindException);
        }
        MyErrorResponse myErrorResponse = new MyErrorResponse(errorCode.getCode(), errorCode.getMessage(), fieldErrorList);
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(myErrorResponse);
    }

    private static List<MyErrorResponse.FieldError> getValidationError(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        List<MyErrorResponse.FieldError> errors = new ArrayList<>(fieldErrors.size());
        for(FieldError fieldError : fieldErrors) {
            errors.add(new MyErrorResponse.FieldError(fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage()));
        }
        return errors;
    }
}
