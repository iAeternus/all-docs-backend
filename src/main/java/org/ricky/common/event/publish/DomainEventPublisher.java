package org.ricky.common.event.publish;

import java.util.List;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className DomainEventPublisher
 * @desc 领域事件发布器
 */
public interface DomainEventPublisher {

    /**
     * 发布
     *
     * @param eventIds 领域事件ID集合
     */
    void publish(List<String> eventIds);

}
