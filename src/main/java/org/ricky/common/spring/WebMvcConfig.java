package org.ricky.common.spring;

import org.ricky.common.converter.type.IntegerCodeToEnumConverterFactory;
import org.ricky.common.converter.type.StringCodeToEnumConverterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className WebMvcConfig
 * @desc Webmvc配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 枚举类的转换器工厂 addConverterFactory
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new IntegerCodeToEnumConverterFactory());
        registry.addConverterFactory(new StringCodeToEnumConverterFactory());
    }

}