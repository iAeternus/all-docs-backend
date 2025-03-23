package org.ricky.core.doc.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.category.domain.task.SyncCategoryTask;
import org.ricky.core.common.domain.event.DomainEvent;
import org.ricky.core.common.domain.event.DomainEventHandler;
import org.ricky.core.common.util.TaskRunner;
import org.ricky.core.doc.domain.event.DocUpdatedEvent;
import org.springframework.stereotype.Component;

import static org.ricky.core.common.domain.event.DomainEventTypeEnum.DOC_UPDATED;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/23
 * @className DocUpdatedEventHandler
 * @desc
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocUpdatedEventHandler implements DomainEventHandler {

    private final SyncCategoryTask syncCategoryTask;

    @Override
    public boolean canHandle(DomainEvent domainEvent) {
        return domainEvent.getType() == DOC_UPDATED;
    }

    @Override
    public void handle(DomainEvent domainEvent, TaskRunner taskRunner) {
        DocUpdatedEvent evt = (DocUpdatedEvent) domainEvent;

        taskRunner.run(() -> syncCategoryTask.run(evt.getCategoryId(), -1));
    }
}
