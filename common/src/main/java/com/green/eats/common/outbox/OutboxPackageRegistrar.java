package com.green.eats.common.outbox;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 스프링 부트의 '자동 스캔 패키지' 목록에 우리 라이브러리의 패키지를
 * 프로그래밍 방식으로 추가합니다.
 */
public class OutboxPackageRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 기존 스캔 목록을 유지하면서 "com.green.eats.common.outbox" 패키지만 추가로 등록합니다.
        AutoConfigurationPackages.register(registry, "com.green.eats.common.outbox");
    }
}
