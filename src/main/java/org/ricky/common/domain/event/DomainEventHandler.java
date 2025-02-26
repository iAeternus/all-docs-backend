package org.ricky.common.domain.event;

import org.ricky.common.util.TaskRunner;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className DomainEventHandler
 * @desc 领域事件处理器
 */
public interface DomainEventHandler {

    /**
     * 判断能否处理
     *
     * @param domainEvent 领域事件
     * @return true=能 false=不能
     */
    boolean canHandle(DomainEvent domainEvent);

    /**
     * 处理领域事件
     *
     * @param domainEvent 领域事件
     * @param taskRunner  任务运行器
     */
    void handle(DomainEvent domainEvent, TaskRunner taskRunner);

    /**
     * 处理失败时的逻辑
     *
     * @param domainEvent 领域事件
     * @param taskRunner  任务运行器
     */
    void handleFailure(DomainEvent domainEvent, TaskRunner taskRunner);

    /**
     * 事件优先级，越小优先级越高
     *
     * @return 优先级
     */
    default int priority() {
        return Integer.MAX_VALUE;
    }

}
