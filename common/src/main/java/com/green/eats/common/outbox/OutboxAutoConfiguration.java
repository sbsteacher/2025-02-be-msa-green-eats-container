package com.green.eats.common.outbox;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
/* [핵심 로직]
 * spring.kafka.producer.key-serializer 설정이 있는지 확인합니다.
 * 프로듀서라면 무조건 설정해야 하는 값이므로, 이 값이 있으면 '프로듀서 서비스'로 판단합니다.
 */
@ConditionalOnProperty(prefix = "spring.kafka.producer", name = "key-serializer")
@EnableScheduling // Relay 스케줄러 활성화

/* 핵심: @Import를 통해 Registrar를 실행합니다.
 * 이제 이 클래스가 빈으로 등록되는 조건(Kafka Producer 설정 존재)일 때만
 * 패키지 스캔 목록에 'common.outbox'가 추가됩니다.
 */
@Import(OutboxPackageRegistrar.class)
public class OutboxAutoConfiguration {
    // 여기에 Relay 등을 @Bean으로 등록하거나,
    // 패키지 스캔 대상에 포함시켜 자동으로 빈이 생성되게 합니다.
}
