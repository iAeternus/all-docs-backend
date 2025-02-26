package org.ricky.common.domain.event;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/5
 * @className DomainEventStatusEnum
 * @desc 领域事件状态枚举
 */
public enum DomainEventStatusEnum {

    /**
     * 已创建
     */
    CREATED,

    /**
     * 发送成功
     */
    PUBLISH_SUCCEED,

    /**
     * 发送失败
     */
    PUBLISH_FAILED,

    /**
     * 消费成功
     */
    CONSUME_SUCCEED,

    /**
     * 消费失败
     */
    CONSUME_FAILED,
    ;

}
