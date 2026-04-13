package com.green.eats.common.enumcode;

import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.EnumSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnumConvertUtils {
    //특정 Enum 클래스에서 code값과 일치하는 Enum 상수를 반환
    public static <E extends Enum<E> & EnumMapperType> E ofCode(Class<E> enumClass, String code) {
        if (StringUtils.isBlank(code)) { return null; }

        return EnumSet.allOf(enumClass) //Enum이 가지고 있는 모든 아이템들을 가진 콜렉션 리턴 (1)
                .stream() // (1)을 스트림 생성
                .filter(item -> item.getCode().equals(code)) //스트림 아이템 중에 원하는 아이템만 다시 스트림 생성한다. (2)
                .findFirst() //(2)스트림 아이템 중 첫번째 아이템을 리턴(Optional) (3)
                .orElse(null); //(3)이 null이었다면 null리턴
//        return EnumSet.allOf(enumClass).stream()
//                .filter(item -> item.getCode().equals(code))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException(
//                        String.format("Unknown code [%s] for enum [%s]", code, enumClass.getSimpleName())));
    }

    public static <E extends Enum<E> & EnumMapperType> String toCode(E enumItem) {
        if (enumItem == null) { return null; }
        return enumItem.getCode();
    }
}
