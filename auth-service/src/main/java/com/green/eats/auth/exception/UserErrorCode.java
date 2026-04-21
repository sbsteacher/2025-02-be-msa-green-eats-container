package com.green.eats.auth.exception;

import com.green.eats.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    CHECK_EMAIL_PASSWORD("U001", "이메일과 비밀번호를 확인해주세요.", HttpStatus.BAD_REQUEST)
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
