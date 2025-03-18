package org.ricky.core.doc.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.properties.SystemProperties;
import org.ricky.core.common.domain.event.DomainEvent;
import org.ricky.core.common.domain.event.DomainEventHandler;
import org.ricky.core.common.util.TaskRunner;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocRepository;
import org.ricky.core.doc.domain.event.DocCreatedEvent;
import org.ricky.core.doc.domain.task.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static org.ricky.core.common.domain.event.DomainEventTypeEnum.DOC_CREATED;
import static org.ricky.core.common.util.FileUtil.deleteFileIfExists;
import static org.ricky.core.common.util.ValidationUtil.isTrue;
import static org.ricky.core.doc.domain.DocStatusEnum.*;

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

    private final DocRepository docRepository;

    private final SystemProperties systemProperties;
    private final SyncTagToDocTask syncTagToDocTask;
    private final DeleteGridFsIfExistsTask deleteGridFsIfExistsTask;
    private final SyncDocToEsTask syncDocToEsTask;
    private final UpdateFileThumbTask updateFileThumbTask;
    private final UpdatePreviewFile updatePreviewFile;

    private final ThreadLocal<DocTaskContext> threadLocalContext = new ThreadLocal<>();

    private static final List<String> SUFFIXES =
            List.of(".pdf", ".docx", ".pptx", ".xlsx", ".html", ".xhtml", ".xht", ".htm", ".md", ".txt", ".jpeg", ".jpg", ".png");

    @Override
    public boolean canHandle(DomainEvent domainEvent) {
        return domainEvent.getType() == DOC_CREATED;
    }

    @Override
    @Transactional
    public void handle(DomainEvent domainEvent, TaskRunner taskRunner) {
        DocCreatedEvent evt = (DocCreatedEvent) domainEvent;
        taskRunner.run(() -> syncTagToDocTask.run(evt.getDocId(), evt.getSuffix()));

        if (isTrue(systemProperties.getAdminReview()) || !SUFFIXES.contains(evt.getSuffix())) {
            return;
        }

        taskRunner.run(() -> deleteGridFsIfExistsTask.run(evt.getDocId(), evt.getTextFileId(), evt.getPreviewFileId(), evt.getThumbId()));

        Doc doc = docRepository.cachedById(evt.getDocId());
        setContext(DocTaskContext.newInstance(doc));
        DocTaskContext context = getContext();
        context.getDoc().updateStatus(ON_PROCESS);

        taskRunner.join();

        taskRunner.run(() -> syncDocToEsTask.run(context));
        taskRunner.run(() -> updateFileThumbTask.run(context));
        taskRunner.run(() -> updatePreviewFile.run(context));

        context.getDoc().updateStatus(SUCCESS);
        docRepository.save(context.getDoc());
    }

    @Override
    public void onFailure(Throwable throwable) {
        log.error("解析文件报错啦", throwable);
        Doc doc = getContext().getDoc();
        doc.updateStatus(FAIL);
        doc.setErrorMsg(throwable.getMessage() + " " + throwable.getCause());
        docRepository.save(doc);
    }

    /**
     * 删除es中的数据，删除thumb数据，删除存储的txt文本文件
     */
    @Override
    public void fallback() {
        DocTaskContext context = getContext();
        String txtFilePath = context.getTxtFilePath();
        String picFilePath = context.getThumbFilePath();
        String previewFilePath = context.getPreviewFilePath();

        // 清除过程中失败的过程文件
        deleteFileIfExists(txtFilePath);
        deleteFileIfExists(picFilePath);
        deleteFileIfExists(previewFilePath);

        // 删除相关的文件
        Set<String> gridFsIds = Stream.of(txtFilePath, picFilePath, previewFilePath)
                .collect(toImmutableSet());
        docRepository.deleteGridFs(context.getDoc().getId(), gridFsIds);

        // TODO 删除es中的数据
    }

    @Override
    public void afterHandle() {
        rmContext();
    }

    private void setContext(DocTaskContext threadLocalContext) {
        this.threadLocalContext.set(threadLocalContext);
    }

    private DocTaskContext getContext() {
        return this.threadLocalContext.get();
    }

    private void rmContext() {
        this.threadLocalContext.remove();
    }
}
