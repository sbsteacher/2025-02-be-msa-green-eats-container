package com.green.eats.order.openfeign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.green.eats.order") // Feign Client 스캔 활성화
public class OpenFeignClientConfiguration { }
