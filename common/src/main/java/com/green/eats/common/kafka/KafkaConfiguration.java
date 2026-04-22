package com.green.eats.common.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJacksonJsonMessageConverter;


@Configuration
@ConditionalOnProperty(prefix = "spring.kafka.consumer", name = "value-deserializer")
public class KafkaConfiguration {
    @Bean
    public RecordMessageConverter converter() {
        /* 들어오는 String 메시지를 Java 객체(UserEvent 등)로
         * 자동으로 역직렬화해주는 컨버터입니다.
         * [Spring Kafka 4.0.4+ 공식 표준]
         * StringJsonMessageConverter의 정식 후계자입니다.
         * Jackson 3 엔진을 사용하여 String 기반 JSON을 객체로 완벽하게 변환합니다.
         */
        return new StringJacksonJsonMessageConverter();
    }
}
