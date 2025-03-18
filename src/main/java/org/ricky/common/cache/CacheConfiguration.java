package org.ricky.common.cache;

import org.ricky.core.common.util.MyObjectMapper;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.tag.domain.Tag;
import org.ricky.core.user.domain.User;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL;
import static java.time.Duration.ofDays;
import static org.ricky.common.constants.ConfigConstant.*;
import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;
import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/10/14
 * @className CacheConfiguration
 * @desc
 */
@Configuration(proxyBeanMethods = false)
public class CacheConfiguration {

    /**
     * 缓存名称前缀
     */
    private static final String CACHE_PREFIX = "Cache:";

    @Bean
    public RedisCacheManagerBuilderCustomizer redisBuilderCustomizer(MyObjectMapper objectMapper) {
        MyObjectMapper defaultObjectMapper = new MyObjectMapper();
        defaultObjectMapper.activateDefaultTyping(defaultObjectMapper.getPolymorphicTypeValidator(), NON_FINAL, PROPERTY);
        GenericJackson2JsonRedisSerializer defaultSerializer = new GenericJackson2JsonRedisSerializer(defaultObjectMapper);

        var userSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, User.class);
        var docSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Doc.class);
        var tagSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Tag.class);

        return builder -> builder.cacheDefaults(defaultCacheConfig()
                        .prefixCacheNameWith(CACHE_PREFIX)
                        .serializeValuesWith(fromSerializer(defaultSerializer))
                        .entryTtl(ofDays(1)))
                .withCacheConfiguration(USER_CACHE, defaultCacheConfig()
                        .prefixCacheNameWith(CACHE_PREFIX)
                        .serializeValuesWith(fromSerializer(userSerializer))
                        .entryTtl(ofDays(7)))
                .withCacheConfiguration(DOC_CACHE, defaultCacheConfig()
                        .prefixCacheNameWith(CACHE_PREFIX)
                        .serializeValuesWith(fromSerializer(docSerializer))
                        .entryTtl(ofDays(7)))
                .withCacheConfiguration(TAG_CACHE, defaultCacheConfig()
                        .prefixCacheNameWith(CACHE_PREFIX)
                        .serializeValuesWith(fromSerializer(tagSerializer))
                        .entryTtl(ofDays(7)));
    }
}
