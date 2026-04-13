package com.green.eats.common;

import com.green.eats.common.auth.UserContextInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CommonWebConfiguration implements WebMvcConfigurer {
    private final UserContextInterceptor userContextInterceptor;
    private final String apiPrefix;

    public CommonWebConfiguration(UserContextInterceptor userContextInterceptor, @Value("${constants.api.prefix:/api}") String apiPrefix) {
        this.userContextInterceptor = userContextInterceptor;
        log.info("============= apiPrefix: {}", apiPrefix);
        this.apiPrefix = apiPrefix;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        if (StringUtils.hasText(apiPrefix)) {
            // 모든 RestController에 프로퍼티로 받은 prefix를 강제 적용
            configurer.addPathPrefix(apiPrefix, HandlerTypePredicate.forAnnotation(RestController.class));
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userContextInterceptor);
    }
}
