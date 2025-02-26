package org.ricky.common.redis;

import io.lettuce.core.RedisBusyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Component;

import static org.ricky.common.constants.ConfigConstant.*;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className RedisStreamInitializer
 * @desc 初始化Redis流和消费者组的组件
 */
@Slf4j
@Component("redisStreamInitializer")
public class RedisStreamInitializer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    public RedisStreamInitializer(RedisTemplate<String, Object> redisTemplate, RedisProperties redisProperties) {
        this.redisTemplate = redisTemplate;
        this.redisProperties = redisProperties;
        ensureConsumerGroupsExist();
    }

    /**
     * 确保所有必要的Redis流消费者组存在<br>
     * 如果消费者组不存在，则尝试创建它们<br>
     */
    private void ensureConsumerGroupsExist() {
        StreamOperations<String, Object, Object> operations = redisTemplate.opsForStream();
        tryCreateConsumerGroup(operations, redisProperties.getDomainEventStream(), REDIS_DOMAIN_EVENT_CONSUMER_GROUP);
        tryCreateConsumerGroup(operations, redisProperties.getWebhookStream(), REDIS_WEBHOOK_CONSUMER_GROUP);
        tryCreateConsumerGroup(operations, redisProperties.getNotificationStream(), REDIS_NOTIFICATION_CONSUMER_GROUP);
    }

    /**
     * 尝试创建Redis流的消费者组
     * 如果消费者组已存在，则捕获RedisBusyException并记录警告
     *
     * @param operations Redis流操作对象
     * @param streamKey  Redis流的键
     * @param group      消费者组的名称
     */
    private void tryCreateConsumerGroup(StreamOperations<String, Object, Object> operations, String streamKey, String group) {
        try {
            operations.createGroup(streamKey, group);
            log.info("Created redis consumer group[{}].", group);
        } catch (RedisSystemException ex) {
            var cause = ex.getRootCause();
            if (cause != null && RedisBusyException.class.equals(cause.getClass())) {
                log.warn("Redis stream group[{}] already exists, skip.", group);
            } else {
                throw ex;
            }
        }
    }

}
