package org.ricky.common.event.publish.interception;

import org.apache.commons.collections4.CollectionUtils;
import org.ricky.core.common.domain.event.DomainEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className ThreadLocalDomainEventIdHolder
 * @desc 记录请求中的所有领域事件ID
 */
public class ThreadLocalDomainEventIdHolder {

    /**
     * 领域事件ID集合
     */
    private static final ThreadLocal<LinkedList<String>> THREAD_LOCAL_EVENT_IDS = ThreadLocal.withInitial(LinkedList::new);

    /**
     * 清空领域事件ID集合
     */
    public static void clear() {
        eventIds().clear();
    }

    /**
     * 释放空间
     */
    public static void remove() {
        THREAD_LOCAL_EVENT_IDS.remove();
    }

    /**
     * 获取所有领域事件ID
     *
     * @return 领域事件ID集合
     */
    public static List<String> allEventIds() {
        LinkedList<String> eventIds = eventIds();
        return CollectionUtils.isNotEmpty(eventIds) ? List.copyOf(eventIds) : List.of();
    }

    /**
     * 添加多个领域事件ID
     *
     * @param events 领域事件ID集合
     */
    public static void addEvents(List<DomainEvent> events) {
        events.forEach(ThreadLocalDomainEventIdHolder::addEvent);
    }

    /**
     * 添加领域事件ID
     *
     * @param event 领域事件ID
     */
    private static void addEvent(DomainEvent event) {
        LinkedList<String> eventIds = eventIds();
        eventIds.addLast(event.getId());
    }

    /**
     * 获取线程全局变量中存储的领域事件ID集合
     *
     * @return 领域事件集合
     */
    private static LinkedList<String> eventIds() {
        return THREAD_LOCAL_EVENT_IDS.get();
    }

}
