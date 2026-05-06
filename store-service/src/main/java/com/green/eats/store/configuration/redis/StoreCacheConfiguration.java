package com.green.eats.store.configuration.redis;

import com.green.eats.common.redis.RedisConfiguration;
import com.green.eats.store.configuration.constants.ConstRedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StoreCacheConfiguration {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        log.info("✅ RedisCacheManager 로딩됨!");

        // 1. common의 표준 가이드를 가져옴
        RedisCacheConfiguration baseConfig = RedisConfiguration.defaultCacheConfig();

        // 2. 이 서비스(Order)만의 개별 설정 정의
        Map<String, RedisCacheConfiguration> configurations = new HashMap<>();
        configurations.put(ConstRedisCache.menuList, baseConfig.entryTtl(Duration.ofHours(24))); // 주문은 길게
        //configurations.put("deliveryStatus", baseConfig.entryTtl(Duration.ofMinutes(1))); // 배송은 짧게

        return RedisCacheManager.builder(connectionFactory)
                                .cacheDefaults(baseConfig.entryTtl(Duration.ofMinutes(30))) // 기본 30분
                                .withInitialCacheConfigurations(configurations)
                                .build();
    }
}
