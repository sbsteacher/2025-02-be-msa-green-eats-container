package com.green.eats.auth.configuration;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = {"com.green.eats.auth", "com.green.eats.common"})
public class EntityConfiguration {
}
