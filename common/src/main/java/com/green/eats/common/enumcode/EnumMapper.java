package com.green.eats.common.enumcode;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EnumMapper {
    private Map<String, List<EnumMapperValue>> factory = new LinkedHashMap<>();

    public void put(String key, Class<? extends EnumMapperType> e) {
        factory.put(key, toEnumValues(e));
    }
    
    // 추가: 이미 변환된 리스트를 직접 저장 (스캐너용)
    public void put(String key, List<EnumMapperValue> values) {
        factory.put(key, values);
    }

    private List<EnumMapperValue> toEnumValues(Class<? extends EnumMapperType> e) {
        return Arrays.stream(e.getEnumConstants()) // Array to Stream
                .map(EnumMapperValue::new) // map은 같은 크기의 스트림을 만든다. 메소드 참조 .map(item -> new EnumMapperValue(item)) 이렇게 작성된 것과 같다.
                .toList(); // 최종연산
    }

    public List<EnumMapperValue> get(String key) {
        return factory.get(key);
    }
}
