package org.ricky.core.common.domain.event;

import java.util.List;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className DomainEventDao
 * @desc 领域事件数据访问对象
 */
public interface DomainEventDao {

    /**
     * 新增领域事件
     *
     * @param events 领域事件
     */
    void insert(List<DomainEvent> events);

    /**
     * 根据事件ID查询领域事件，没找到抛异常
     *
     * @param id 事件ID
     * @return 领域事件
     */
    DomainEvent byId(String id);

    /**
     * 根据事件ID列表批量查询领域事件
     *
     * @param ids 事件ID列表
     * @return 领域事件列表，没找到返回空列表
     */
    List<DomainEvent> byIds(List<String> ids);

    /**
     * 查询最后发生的那个事件
     *
     * @param arId       聚合根ID
     * @param type       领域事件类型
     * @param eventClass 领域事件字节码
     * @return 返回最后发生的那个事件
     */
    <T extends DomainEvent> T latestEventFor(String arId, DomainEventTypeEnum type, Class<T> eventClass);

    /**
     * 成功发布事件后的回调函数
     *
     * @param event 领域事件
     */
    void successPublish(DomainEvent event);

    /**
     * 失败发布事件后的回调函数
     *
     * @param event 领域事件
     */
    void failPublish(DomainEvent event);

    /**
     * 成功消费事件后的回调函数
     *
     * @param event 领域事件
     */
    void successConsume(DomainEvent event);

    /**
     * 失败消费事件后的回调函数
     *
     * @param event 领域事件
     */
    void failConsume(DomainEvent event);

    /**
     * 查找将要发布的事件
     *
     * @param startId 起始ID，将查找比这个ID大的事件
     * @param limit   事件数限制
     * @return 将要发布的事件列表
     */
    List<DomainEvent> tobePublishedEvents(String startId, int limit);
}
