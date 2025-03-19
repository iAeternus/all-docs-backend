package org.ricky.core.doc.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.common.domain.event.DomainEvent;
import org.ricky.core.common.domain.event.DomainEventHandler;
import org.ricky.core.common.util.TaskRunner;
import org.springframework.stereotype.Component;

import static org.ricky.core.common.domain.event.DomainEventTypeEnum.DOC_SEARCHED;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className DocSearchedEventHandler
 * @desc
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocSearchedEventHandler implements DomainEventHandler {
    @Override
    public boolean canHandle(DomainEvent domainEvent) {
        return domainEvent.getType() == DOC_SEARCHED;
    }

    @Override
    public void handle(DomainEvent domainEvent, TaskRunner taskRunner) {
        // TODO 维护热词
    }
}
