package org.ricky.common.spring;

import org.ricky.core.common.util.MyObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import static java.time.Duration.ofSeconds;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className SpringCommonConfiguration
 * @desc Spring通用配置类
 */
@EnableCaching
@EnableAsync
@EnableRetry
@Configuration
public class SpringCommonConfiguration {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(ofSeconds(10))
                .setReadTimeout(ofSeconds(10))
                .build();
    }

    @Bean
    public MyObjectMapper objectMapper() {
        return new MyObjectMapper();
    }

}
