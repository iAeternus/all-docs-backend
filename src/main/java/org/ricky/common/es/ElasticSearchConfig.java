package org.ricky.common.es;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.ricky.common.constants.ConfigConstant.LOCAL_DATE_TIME_PATTERN;

@Component
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private List<String> uris;

    @NonNull
    @Override
    public ClientConfiguration clientConfiguration() {
        // 去除http开头
        uris = uris.stream().map(uri -> {
            if (uri.startsWith("http")) {
                uri = uri.split("://")[1];
            }
            return uri;
        }).toList();
        return ClientConfiguration.builder()
                .connectedTo(uris.toArray(new String[0]))
                .build();
    }

    @Bean
    @NonNull
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        // 注入自定义转换器处理LocalDateTime数据
        List<Converter<?, ?>> converters = new ArrayList<>(16);
        converters.add(StringToLocalDateTimeConverter.INSTANCE);
        converters.add(LocalDateTimeToStringConverter.INSTANCE);
        return new ElasticsearchCustomConversions(converters);
    }

    /**
     * LocalDateTime转String
     */
    @WritingConverter
    private enum LocalDateTimeToStringConverter implements Converter<LocalDateTime, String> {
        /**
         * 实例化
         */
        INSTANCE;

        @Override
        public String convert(LocalDateTime source) {
            return source.format(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_PATTERN));
        }
    }

    /**
     * String转LocalDateTime
     */
    @ReadingConverter
    private enum StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
        /**
         * 实例化
         */
        INSTANCE;

        @Override
        public LocalDateTime convert(@NonNull String source) {
            return LocalDateTime.parse(source, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_PATTERN));
        }
    }
}
