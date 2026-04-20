package com.green.eats.common.enumcode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@ComponentScan(basePackages = "com.green.eats.common") // CommonController 스캔용
public class EnumAutoConfiguration {

    @Bean
    public EnumMapper enumMapper(ApplicationContext applicationContext
            , @Value("${constants.enum.scan-package:null}") String scanPackage) {
        EnumMapper enumMapper = new EnumMapper();
        log.info("scanPackage: {}", scanPackage);
        //1. 스캔할 패키지 리스트 준비
        List<String> scanPackages = new ArrayList<>();
        scanPackages.add(getBasePackage(applicationContext)); //메인 애플리케이션의 패키지 경로를 가져와서 스캔
        if(scanPackage != null) {
            scanPackages.add(scanPackage);
        }

        for(String basePackage : scanPackages) {
            Map<String, List<EnumMapperValue>> scannedCodes = EnumMapperScanner.scan(basePackage);
            //scannedCodes.forEach(enumMapper::put);
            scannedCodes.forEach((key, values) -> {
                // 기존 EnumMapper의 put 로직을 직접 수행하거나 factory에 주입
                // 여기서는 Scanner가 이미 변환했으므로 EnumMapper에 해당 데이터를 넣는 메서드 필요
                enumMapper.put(key, values);
            });
        }

        return enumMapper;
    }

    private String getBasePackage(ApplicationContext context) {
        // @SpringBootApplication이 붙은 클래스의 패키지를 찾음
        return context.getBeansWithAnnotation(SpringBootApplication.class)
                .values().iterator().next().getClass().getPackageName();
    }
}