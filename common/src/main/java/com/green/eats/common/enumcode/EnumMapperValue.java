package com.green.eats.common.enumcode;

//프론트엔드에 Enum 정보를 전달하기 위한 Record
public record EnumMapperValue(String code, String value) {
    public EnumMapperValue(EnumMapperType enumMapperType) {
        this(enumMapperType.getCode(), enumMapperType.getValue());
    }
}
