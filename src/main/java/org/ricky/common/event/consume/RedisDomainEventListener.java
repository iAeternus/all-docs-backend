package org.ricky.common.event.consume;

import io.micrometer.tracing.ScopedSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.tracing.TracingService;
import org.ricky.core.common.util.MyObjectMapper;
import org.ricky.core.common.domain.event.DomainEvent;
import org.ricky.core.common.domain.event.DomainEventConsumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className RedisDomainEventListener
 * @desc
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDomainEventListener implements DomainEventListener, StreamListener<String, ObjectRecord<String, String>> {

    private final MyObjectMapper myObjectMapper;
    private final DomainEventConsumer domainEventConsumer;
    private final TracingService tracingService;

    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        ScopedSpan scopedSpan = tracingService.startNewSpan("domain-event-listener");

        String jsonString = message.getValue();
        DomainEvent domainEvent = myObjectMapper.readValue(jsonString, DomainEvent.class);
        try {
            domainEventConsumer.consume(domainEvent);
        } catch (Throwable t) {
            log.error("Failed to listen domain event[{}:{}].", domainEvent.getType(), domainEvent.getId(), t);
        }

        scopedSpan.end();
    }
}
