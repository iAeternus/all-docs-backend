package org.ricky.core.doc.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.category.domain.event.CategoryDeletedEvent;
import org.ricky.core.common.domain.event.DomainEvent;
import org.ricky.core.common.domain.event.DomainEventHandler;
import org.ricky.core.common.util.TaskRunner;
import org.ricky.core.doc.domain.task.SyncDocToCategoryTask;
import org.springframework.stereotype.Component;

import static org.ricky.core.common.domain.event.DomainEventTypeEnum.CATEGORY_DELETED;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className CategoryDeletedEventHandler
 * @desc
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryDeletedEventHandler implements DomainEventHandler {

    private final SyncDocToCategoryTask syncDocToCategoryTask;

    @Override
    public boolean canHandle(DomainEvent domainEvent) {
        return domainEvent.getType() == CATEGORY_DELETED;
    }

    @Override
    public void handle(DomainEvent domainEvent, TaskRunner taskRunner) {
        CategoryDeletedEvent evt = (CategoryDeletedEvent) domainEvent;
        taskRunner.run(() -> syncDocToCategoryTask.run(evt.getCategoryId(), evt.getIsDeleteFile()));
    }
}
