package com.green.eats.common.model;

import com.green.eats.common.enumcode.AbstractEnumCodeConverter;
import com.green.eats.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnumUserRole implements EnumMapperType {
    USER("01", "일반유저"),
    ADMIN("02", "관리자")
    ;
    private final String code;
    private final String value;

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<EnumUserRole> {
        public CodeConverter() { super(EnumUserRole.class, false); }
    }
}
