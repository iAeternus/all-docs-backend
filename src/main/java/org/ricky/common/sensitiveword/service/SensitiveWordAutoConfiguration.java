package org.ricky.common.sensitiveword.service;

import jakarta.annotation.Resource;
import org.ricky.common.sensitiveword.domain.SensitiveWordRepository;
import org.ricky.common.sensitiveword.domain.filter.impl.DFAFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author Ricky
 * @version 1.0
 * @date 2024/8/21
 * @className SensitiveWordAutoConfiguration
 * @desc 敏感词自动配置类
 */
@Configuration
public class SensitiveWordAutoConfiguration {

    @Resource
    private SensitiveWordRepository sensitiveWordRepository;

    @Bean
    public SensitiveWordService sensitiveWordService() {
        return SensitiveWordService.newInstance()
                .filterStrategy(DFAFilter.getInstance())
                .sensitiveWord(sensitiveWordRepository)
                .init();
    }

}
