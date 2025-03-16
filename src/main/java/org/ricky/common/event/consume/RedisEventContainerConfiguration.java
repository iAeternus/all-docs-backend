package org.ricky.common.event.consume;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.redis.RedisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.util.ErrorHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.stream.IntStream;

import static org.ricky.common.constants.ConfigConstant.REDIS_DOMAIN_EVENT_CONSUMER_GROUP;
import static org.springframework.data.redis.connection.stream.Consumer.from;
import static org.springframework.data.redis.connection.stream.ReadOffset.lastConsumed;
import static org.springframework.data.redis.connection.stream.StreamOffset.create;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className RedisEventContainerConfiguration
 * @desc Redis事件容器配置类
 */
@Slf4j
@Profile("!dev")
@Configuration
@DependsOn("redisStreamInitializer")
@RequiredArgsConstructor
public class RedisEventContainerConfiguration {

    private final RedisProperties redisProperties;

    @Qualifier("redisDomainEventListener") // TODO
    private final DomainEventListener domainEventListener;

    @Qualifier("consumeDomainEventTaskExecutor")
    private final TaskExecutor consumeDomainEventTaskExecutor;

    @Bean
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> domainEventContainer(RedisConnectionFactory factory) {
        if (!(domainEventListener instanceof RedisDomainEventListener redisDomainEventListener)) {
            throw new RuntimeException("事件监听器不匹配");
        }

        var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .batchSize(10)
                .executor(consumeDomainEventTaskExecutor)
                .targetType(String.class)
                .errorHandler(new MyRedisErrorHandler())
                .build();

        var container = StreamMessageListenerContainer.create(factory, options);

        // 25个consumer，对应25个线程，每个consumer线程负责拉取消息并处理
        IntStream.range(1, 26).forEach(index -> {
            try {
                container.receiveAutoAck(
                        from(REDIS_DOMAIN_EVENT_CONSUMER_GROUP, InetAddress.getLocalHost().getHostName() + "-" + index),
                        create(redisProperties.getDomainEventStream(), lastConsumed()),
                        redisDomainEventListener
                );
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        });

        container.start();
        return container;
    }

    @Slf4j
    private static class MyRedisErrorHandler implements ErrorHandler {
        @Override
        public void handleError(Throwable t) {
            log.error(t.getMessage());
        }
    }

}
