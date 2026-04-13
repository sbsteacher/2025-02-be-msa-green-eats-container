package com.green.eats.common.enumcode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.*;

@Slf4j
public class EnumMapperScanner {

    public static Map<String, List<EnumMapperValue>> scan(String basePackage) {
        Map<String, List<EnumMapperValue>> factory = new LinkedHashMap<>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);

        // EnumMapperType을 구현한 클래스만 필터링
        scanner.addIncludeFilter(new AssignableTypeFilter(EnumMapperType.class));

        Set<BeanDefinition> components = scanner.findCandidateComponents(basePackage);

        for (BeanDefinition component : components) {
            try {
                Class<?> clazz = Class.forName(component.getBeanClassName());
                if (clazz.isEnum() && EnumMapperType.class.isAssignableFrom(clazz)) {
                    Class<? extends EnumMapperType> enumClass = (Class<? extends EnumMapperType>) clazz;

                    // Enum 이름을 PascalCase -> camelCase로 변환
                    String key = convertToCamelCase(clazz.getSimpleName());
                    factory.put(key, toEnumValues(enumClass));
                    log.info("Enum Registered: {} as key '{}'", clazz.getSimpleName(), key);
                }
            } catch (ClassNotFoundException e) {
                log.error("Enum scan error", e);
            }
        }
        return factory;
    }

    private static List<EnumMapperValue> toEnumValues(Class<? extends EnumMapperType> e) {
        return Arrays.stream(e.getEnumConstants())
                .map(EnumMapperValue::new)
                .toList();
    }

    private static String convertToCamelCase(String name) {
        // "EnumMenuCategory" -> "menuCategory" 또는 "MenuCategory" -> "menuCategory"
        // "Enum" 프리픽스 제거 로직 포함 (선택사항)
        String target = name.startsWith("Enum") ? name.substring(4) : name;
        return Character.toLowerCase(target.charAt(0)) + target.substring(1);
    }
}
