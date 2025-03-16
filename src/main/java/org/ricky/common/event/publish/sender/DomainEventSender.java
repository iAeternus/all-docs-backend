package org.ricky.common.event.publish.sender;

import org.ricky.core.common.domain.event.DomainEvent;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className DomainEventSender
 * @desc 领域事件发送器，关注领域事件的发送
 * 与发布器的区别是发送器更加底层，与Redis或kafka打交道
 */
public interface DomainEventSender {

    /**
     * 发送领域事件
     *
     * @param event 领域事件
     */
    void send(DomainEvent event);

}
