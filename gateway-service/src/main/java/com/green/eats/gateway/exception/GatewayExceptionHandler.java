package com.green.eats.gateway.exception;

import com.green.eats.common.exception.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.concurrent.TimeoutException;

import static com.green.eats.common.exception.ServiceExceptionHandler.handleExceptionInternal;

@Slf4j
@RestControllerAdvice
public class GatewayExceptionHandler {

    // 없는 경로인 경우
    @ExceptionHandler({ NoHandlerFoundException.class, NoResourceFoundException.class })
    public ResponseEntity<Object> handleResponseStatus(Exception e) {
        return handleExceptionInternal(CommonErrorCode.NOT_FOUND_PATH);
    }

    // 1. 라우팅 대상 서비스를 찾지 못하거나 연결할 수 없는 경우 (503)
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Object> handleConnectionError(Exception e) {
        return handleExceptionInternal(CommonErrorCode.SERVICE_UNAVAILABLE);
    }

    // 2. 서비스 응답 시간이 초과된 경우 (504)
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<Object> handleTimeoutError(TimeoutException e) {
        return handleExceptionInternal(CommonErrorCode.GATEWAY_TIMEOUT);
    }

    // 3. 그 외 게이트웨이 자체 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralError(Exception e) {
        return handleExceptionInternal(CommonErrorCode.GATEWAY_INTERNAL_ERROR);
    }
}
