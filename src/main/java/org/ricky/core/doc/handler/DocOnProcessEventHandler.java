package org.ricky.core.doc.handler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.common.domain.event.DomainEvent;
import org.ricky.core.common.domain.event.DomainEventHandler;
import org.ricky.common.util.TaskRunner;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocRepository;
import org.ricky.core.doc.domain.event.DocOnProcessEvent;
import org.ricky.core.doc.domain.task.*;
import org.springframework.stereotype.Component;

import static org.ricky.core.common.domain.event.DomainEventTypeEnum.DOC_ON_PROCESS;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DocOnProcessEventHandler
 * @desc 处理`进行中`状态
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocOnProcessEventHandler implements DomainEventHandler {

    private final DocRepository docRepository;

    private final DeleteGridFsIfExistsTask deleteGridFsIfExistsTask;
    private final SyncDocToEsTask syncDocToEsTask;
    private final UpdateFileThumbTask updateFileThumbTask;
    private final UpdatePreviewFile updatePreviewFile;

    @Override
    public boolean canHandle(DomainEvent domainEvent) {
        return domainEvent.getType() == DOC_ON_PROCESS;
    }

    @Override
    public void handle(DomainEvent domainEvent, TaskRunner taskRunner) {
        DocOnProcessEvent evt = (DocOnProcessEvent) domainEvent;
        taskRunner.run(() -> deleteGridFsIfExistsTask.run(evt.getTextFileId(), evt.getPreviewFileId(), evt.getThumbId()));
        // TODO 异步需要 join

        Doc doc = docRepository.cachedById(evt.getDocId());
        DocTaskContext context = DocTaskContext.newInstance(doc);
        taskRunner.run(() -> syncDocToEsTask.run(context));
        taskRunner.run(() -> updateFileThumbTask.run(context));
        taskRunner.run(() -> updatePreviewFile.run(context));
    }
}
