package org.ricky.common.ratelimit;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ricky.common.exception.MyException;
import org.ricky.common.properties.SystemProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.ricky.common.exception.ErrorCodeEnum.TOO_MANY_REQUEST;
import static org.ricky.core.common.util.ValidationUtil.requireNotBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className RedisRateLimiter
 * @desc 使用Redis计数器实现的限流
 */
@Component("customRedisRateLimiter")
@RequiredArgsConstructor
public class RedisRateLimiter implements RateLimiter {

    private final StringRedisTemplate stringRedisTemplate;

    private final SystemProperties systemProperties;

    /**
     * Redis限流键前缀
     */
    private static final String KET_PREFIX = "RateLimit:";

    /**
     * 时间窗口长度
     */
    private static final int TIME_WINDOW_LENGTH = 5;

    @Override
    public void applyFor(String uid, String key, int tps) {
        requireNotBlank(uid, "UID must not be blank.");
        requireNotBlank(key, "Key must not be blank.");

        // 以5秒为周期统计
        doApply(
                key + ":" + uid + ":" + Instant.now().getEpochSecond() / TIME_WINDOW_LENGTH,
                tps * TIME_WINDOW_LENGTH,
                TIME_WINDOW_LENGTH,
                TimeUnit.SECONDS
        );
    }

    @Override
    public void applyFor(String key, int tps) {
        requireNotBlank(key, "Key must not be blank.");

        // 以5秒为周期统计
        doApply(
                key + ":" + Instant.now().getEpochSecond() / TIME_WINDOW_LENGTH,
                tps * TIME_WINDOW_LENGTH,
                TIME_WINDOW_LENGTH,
                TimeUnit.SECONDS
        );
    }

    /**
     * 执行限流，被限流时抛出异常
     *
     * @param key        请求键-请求的唯一标识
     * @param limit      流量限制
     * @param expire     有效时间
     * @param expireUnit 有效时间单位
     */
    private void doApply(String key, int limit, int expire, TimeUnit expireUnit) {
        if (!systemProperties.getEnableLimitRate()) {
            return;
        }

        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be greater than 1.");
        }

        String finalKey = KET_PREFIX + key;
        String count = stringRedisTemplate.opsForValue().get(finalKey);
        if (StringUtils.isNotBlank(count) && Integer.parseInt(count) >= limit) {
            throw new MyException(TOO_MANY_REQUEST, "当前请求量过大。", Map.of("key", finalKey));
        }

        // 增加计数器的值，并设置或更新键的过期时间（即时间窗口的长度）
        stringRedisTemplate.execute(new SessionCallback<>() {
            @Override
            public <K, V> Object execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                final StringRedisTemplate redisTemplate = (StringRedisTemplate) operations;
                final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
                operations.multi();
                valueOperations.increment(finalKey);
                redisTemplate.expire(finalKey, expire, expireUnit);
                return operations.exec();
            }
        });
    }
}
