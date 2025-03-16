package org.ricky.core.doc.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.common.domain.event.DomainEvent;
import org.ricky.core.common.domain.event.DomainEventHandler;
import org.ricky.common.properties.SystemProperties;
import org.ricky.common.util.TaskRunner;
import org.ricky.core.doc.domain.event.DocCreatedEvent;
import org.ricky.core.doc.domain.task.UpdateDocStatusTask;
import org.ricky.core.doc.domain.task.SyncTagToDocTask;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.ricky.core.common.domain.event.DomainEventTypeEnum.DOC_CREATED;
import static org.ricky.common.util.ValidationUtil.isFalse;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DocCreatedEventHandler
 * @desc
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocCreatedEventHandler implements DomainEventHandler {

    private final SystemProperties systemProperties;
    private final SyncTagToDocTask syncTagToDocTask;
    private final UpdateDocStatusTask updateDocStatusTask;

    private static final List<String> SUFFIXES =
            List.of("pdf", "docx", "pptx", "xlsx", "html", "xhtml", "xht", "htm", "md", "txt", "jpeg", "jpg", "png");

    @Override
    public boolean canHandle(DomainEvent domainEvent) {
        return domainEvent.getType() == DOC_CREATED;
    }

    @Override
    public void handle(DomainEvent domainEvent, TaskRunner taskRunner) {
        DocCreatedEvent evt = (DocCreatedEvent) domainEvent;
        taskRunner.run(() -> syncTagToDocTask.run(evt.getDocId(), evt.getSuffix()));
        if (isFalse(systemProperties.getAdminReview()) || SUFFIXES.contains(evt.getSuffix())) {
            taskRunner.run(() -> updateDocStatusTask.run(evt.getDocId()));
        }
    }
}
