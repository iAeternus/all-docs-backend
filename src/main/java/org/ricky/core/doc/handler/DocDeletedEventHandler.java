package org.ricky.core.doc.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.common.domain.event.DomainEvent;
import org.ricky.core.common.domain.event.DomainEventHandler;
import org.ricky.core.common.util.TaskRunner;
import org.springframework.stereotype.Component;

import static org.ricky.core.common.domain.event.DomainEventTypeEnum.DOC_DELETED;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/18
 * @className DocDeletedEventHandler
 * @desc
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocDeletedEventHandler implements DomainEventHandler {

    @Override
    public boolean canHandle(DomainEvent domainEvent) {
        return domainEvent.getType() == DOC_DELETED;
    }

    @Override
    public void handle(DomainEvent domainEvent, TaskRunner taskRunner) {
        /*
        评论
        分类
        收藏
        标签（Optional）
        评审信息
        文档es索引
        统计信息
        */
        // TODO 删除关联表
    }
}
