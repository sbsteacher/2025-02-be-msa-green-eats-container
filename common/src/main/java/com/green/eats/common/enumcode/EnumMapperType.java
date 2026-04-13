package com.green.eats.common.enumcode;

import com.fasterxml.jackson.annotation.JsonValue;

//Code로 사용하는 모든 Enum이 공통적으로 구현해야하는 인터페이스
public interface EnumMapperType {
    String getCode();
    @JsonValue
    String getValue();
}
