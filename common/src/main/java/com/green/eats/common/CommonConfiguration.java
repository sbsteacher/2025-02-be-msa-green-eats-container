package com.green.eats.common;

import com.green.eats.common.constants.ConstJwt;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.green.eats.common")
@ConfigurationPropertiesScan("com.green.eats.common")
@EnableConfigurationProperties(ConstJwt.class)
public class CommonConfiguration { }