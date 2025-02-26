package org.ricky.common.event.publish.interception;

import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className DomainEventInterceptorConfigurer
 * @desc 注册拦截器
 */
@Configuration
public class DomainEventInterceptorConfigurer implements WebMvcConfigurer {

    private final DomainEventHandlingInterceptor domainEventHandlingInterceptor;

    public DomainEventInterceptorConfigurer(DomainEventHandlingInterceptor interceptor) {
        this.domainEventHandlingInterceptor = interceptor;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(domainEventHandlingInterceptor);
    }
}
