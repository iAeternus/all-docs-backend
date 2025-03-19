package org.ricky.core.category.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.category.domain.event.CategoryDisconnectedEvent;
import org.ricky.core.category.domain.task.SyncCategoryTask;
import org.ricky.core.common.domain.event.DomainEvent;
import org.ricky.core.common.domain.event.DomainEventHandler;
import org.ricky.core.common.util.TaskRunner;
import org.springframework.stereotype.Component;

import static org.ricky.core.common.domain.event.DomainEventTypeEnum.CATEGORY_DISCONNECTED;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className CategoryDisconnectedEventHandler
 * @desc
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryDisconnectedEventHandler implements DomainEventHandler {

    private final SyncCategoryTask syncCategoryTask;

    @Override
    public boolean canHandle(DomainEvent domainEvent) {
        return domainEvent.getType() == CATEGORY_DISCONNECTED;
    }

    @Override
    public void handle(DomainEvent domainEvent, TaskRunner taskRunner) {
        CategoryDisconnectedEvent evt = (CategoryDisconnectedEvent) domainEvent;
        taskRunner.run(() -> syncCategoryTask.run(evt.getCategoryId(), evt.getDCnt()));
    }
}
