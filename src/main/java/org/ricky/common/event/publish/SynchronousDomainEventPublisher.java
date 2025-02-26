package org.ricky.common.event.publish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.domain.event.DomainEvent;
import org.ricky.common.domain.event.DomainEventConsumer;
import org.ricky.common.domain.event.DomainEventDao;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className SynchronousDomainEventPublisher
 * @desc 同步方式发布领域事件<br>
 * 可能导致系统响应时间延长，在高并发场景下可能影响系统吞吐量<br>
 * 建议采用异步方式<br>
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class SynchronousDomainEventPublisher implements DomainEventPublisher {

    private final DomainEventDao domainEventDao;
    private final DomainEventConsumer domainEventConsumer;

    @Override
    public void publish(List<String> eventIds) {
        List<DomainEvent> domainEvents = domainEventDao.byIds(eventIds);
        domainEvents.forEach(domainEvent -> {
            try {
                domainEventConsumer.consume(domainEvent);
            } catch (Throwable t) {
                log.error("Consume domain event[{}:{}] failed.", domainEvent.getType(), domainEvent.getId(), t);
            }
        });
    }
}
