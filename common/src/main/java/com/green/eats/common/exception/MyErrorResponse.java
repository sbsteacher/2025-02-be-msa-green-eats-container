package com.green.eats.common.exception;

import java.util.List;

public record MyErrorResponse (String code,        // 커스텀 에러 코드 (예: STORE-001)
                               String message,     // 에러 메시지
                               List<FieldError> errors // 유효성 검사 실패 상세 (선택)
) {
    public record FieldError(String field, Object value, String reason) {}
}
