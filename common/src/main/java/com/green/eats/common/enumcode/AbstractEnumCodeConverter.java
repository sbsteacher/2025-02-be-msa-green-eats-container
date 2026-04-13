package com.green.eats.common.enumcode;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;

//JPA Entity 필드와 DB 컬럼 간의 자동 변환을 담당하는 추상 클래스
@RequiredArgsConstructor
public abstract class AbstractEnumCodeConverter<E extends Enum<E> & EnumMapperType>
        implements AttributeConverter<E, String> {

    private final Class<E> targetEnumClass;
    private final boolean nullable; //null가능

    @Override
    public String convertToDatabaseColumn(E enumItem) {
        if(!nullable && enumItem == null) { //null허용을 하지 않았는데 attribute값이 null이었다면
            throw new IllegalArgumentException(String.format("%s(는)은 NULL로 저장할 수 없습니다.", targetEnumClass.getSimpleName()));
        }
        return EnumConvertUtils.toCode(enumItem);
    }

    @Override
    public E convertToEntityAttribute(String dbData) {
        if(!nullable && StringUtils.isBlank(dbData) || dbData == null) {
            throw new IllegalArgumentException(String.format("%s(는)가 DB에 NULL 혹은 Empty로 저장되어 있습니다.", targetEnumClass.getSimpleName()));
        }
        return EnumConvertUtils.ofCode(targetEnumClass, dbData);
    }
}