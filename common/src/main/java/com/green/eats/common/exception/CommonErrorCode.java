package com.green.eats.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
      INVALID_INPUT_VALUE("C001", "잘못된 입력값입니다.",HttpStatus.BAD_REQUEST)
    , INTERNAL_SERVER_ERROR("C002", "게이트웨이 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR)
    , SERVICE_UNAVAILABLE("C003", "현재 서비스를 이용할 수 없습니다.", HttpStatus.SERVICE_UNAVAILABLE)
    , GATEWAY_TIMEOUT("C004", "서비스 응답 시간이 초과되었습니다.", HttpStatus.GATEWAY_TIMEOUT)
    , GATEWAY_INTERNAL_ERROR("C005", "게이트웨이 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR)
    , NO_EXISTED_USER("C006", "사용자 정보가 없습니다.", HttpStatus.BAD_REQUEST)
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
