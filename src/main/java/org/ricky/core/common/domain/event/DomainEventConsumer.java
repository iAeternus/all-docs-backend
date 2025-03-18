package org.ricky.core.common.domain.event;

import lombok.extern.slf4j.Slf4j;
import org.ricky.core.common.util.TaskRunner;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className DomainEventConsumer
 * @desc 领域事件消费者
 */
@Slf4j
@Component
public class DomainEventConsumer {

    private final List<DomainEventHandler> handlers;
    private final DomainEventDao domainEventDao;

    public DomainEventConsumer(List<DomainEventHandler> handlers, DomainEventDao domainEventDao) {
        this.handlers = handlers;
        // 按优先级排序，最优先在最前面
        this.handlers.sort(Comparator.comparingInt(DomainEventHandler::priority));
        this.domainEventDao = domainEventDao;
    }

    /**
     * 消费事件<br>
     * 所有能处理事件的handler依次处理，全部处理成功记录消费成功，否则记录为消费失败；<br>
     * 消费失败后，兜底机制将重新发送事件，重新发送最多不超过3次<br>
     *
     * @param domainEvent 领域事件
     */
    public void consume(DomainEvent domainEvent) {
        log.info("Start consume domain event[{}:{}].", domainEvent.getType(), domainEvent.getId());

        boolean hasError = false;
        TaskRunner taskRunner = TaskRunner.newTaskRunner();

        for (DomainEventHandler handler : handlers) {
            try {
                if (handler.canHandle(domainEvent)) {
                    handler.handle(domainEvent, taskRunner);
                }
            } catch (Throwable t) {
                hasError = true;
                handler.onFailure(t);
                handler.fallback();
                log.error("Error while handle domain event[{}:{}] by [{}].",
                        domainEvent.getType(), domainEvent.getId(), handler.getClass().getSimpleName(), t);
            } finally {
                handler.afterHandle();
            }
        }

        if (taskRunner.hasError()) {
            hasError = true;
        }

        if (hasError) {
            domainEventDao.failConsume(domainEvent);
        } else {
            domainEventDao.successConsume(domainEvent);
        }
    }

}
