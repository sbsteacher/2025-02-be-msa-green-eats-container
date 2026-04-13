package com.green.eats.store.configuration;

import com.green.eats.common.enumcode.EnumMapper;
import com.green.eats.store.enumcode.EnumMenuCategory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
    @Bean
    public EnumMapper enumMapper() {
        EnumMapper enumMapper = new EnumMapper();
        enumMapper.put("menuCategory", EnumMenuCategory.class);
        return enumMapper;
    }
}
