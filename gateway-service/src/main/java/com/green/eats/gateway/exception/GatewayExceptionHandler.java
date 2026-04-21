package com.green.eats.gateway.exception;

import com.green.eats.common.exception.CommonErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

import static com.green.eats.common.exception.ServiceExceptionHandler.handleExceptionInternal;

@RestControllerAdvice
public class GatewayExceptionHandler {

    // 1. 라우팅 대상 서비스를 찾지 못하거나 연결할 수 없는 경우 (503)
    @ExceptionHandler({ConnectException.class, IOException.class})
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
