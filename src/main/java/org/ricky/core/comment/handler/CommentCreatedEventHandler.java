package org.ricky.core.comment.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.common.domain.event.DomainEvent;
import org.ricky.core.common.domain.event.DomainEventHandler;
import org.ricky.core.common.util.TaskRunner;
import org.springframework.stereotype.Component;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentCreatedEventHandler
 * @desc
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentCreatedEventHandler implements DomainEventHandler {
    @Override
    public boolean canHandle(DomainEvent domainEvent) {
        return false;
    }

    @Override
    public void handle(DomainEvent domainEvent, TaskRunner taskRunner) {
        // TODO 同步文档等操作
    }
}
