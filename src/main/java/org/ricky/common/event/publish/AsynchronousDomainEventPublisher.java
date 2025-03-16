package org.ricky.common.event.publish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.ricky.core.common.domain.event.DomainEvent;
import org.ricky.core.common.domain.event.DomainEventDao;
import org.ricky.common.event.publish.sender.DomainEventSender;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className AsynchronousDomainEventPublisher
 * @desc 异步领域事件发布器
 */
@Slf4j
@Component
@Profile("!dev")
@RequiredArgsConstructor
public class AsynchronousDomainEventPublisher implements DomainEventPublisher {

    private final DomainEventDao domainEventDao;

    @Qualifier("redisDomainEventSender")
    private final DomainEventSender domainEventSender;
    private final TaskExecutor taskExecutor;

    @Override
    public void publish(List<String> eventIds) {
        if (CollectionUtils.isNotEmpty(eventIds)) {
            taskExecutor.execute(() -> {
                // 根据事件ID，从事件发布表中加载相应事件
                List<DomainEvent> domainEvents = domainEventDao.byIds(eventIds);
                // 发布事件
                domainEvents.forEach(domainEventSender::send);
            });
        }
    }
}
