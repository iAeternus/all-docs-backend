package org.ricky.core.common.domain;

import lombok.AccessLevel;
import lombok.Getter;
import org.ricky.core.common.context.ThreadLocalContext;
import org.ricky.core.common.domain.event.DomainEvent;
import org.springframework.data.annotation.Version;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.time.Instant.now;
import static org.ricky.common.util.ValidationUtil.isNull;
import static org.ricky.common.util.ValidationUtil.requireNotBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className AggregateRoot
 * @desc 聚合根<br>
 * 规范：<br>
 * 1. 实现类必须将无参构造私有<br>
 * 2. 实现类不允许有默认的setter<br>
 */
@Getter
public abstract class AggregateRoot implements Identified {

    /**
     * 最多保留的操作日志数量
     */
    public static final int MAX_OPS_LOG_SIZE = 128;

    /**
     * 标识符，通过Snowflake算法生成
     */
    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 创建时间
     */
    private Instant createdAt;

    /**
     * 创建人的UID
     */
    private String createdBy;

    /**
     * 创建人姓名
     */
    private String creator;

    /**
     * 更新时间
     */
    private Instant updatedAt;

    /**
     * 更新人UID
     */
    private String updatedBy;

    /**
     * 更新人姓名
     */
    private String updater;

    /**
     * 领域事件列表，用于临时存放完成某个业务流程中所发出的事件，会被BaseRepository保存到事件表中
     */
    private List<DomainEvent> events;

    /**
     * 操作日志
     */
    private LinkedList<OpsLog> opsLogs;

    /**
     * 版本号，实现乐观锁
     */
    @Version
    @Getter(AccessLevel.PRIVATE)
    private Long _version;

    protected AggregateRoot() {
        this.clearEvents();
    }

    protected AggregateRoot(String id) {
        requireNotBlank(id, "ID must not be blank.");

        this.id = id;
        this.userId = ThreadLocalContext.getContext().getUid();
        this.createdAt = now();
        this.createdBy = ThreadLocalContext.getContext().getUid();
        this.creator = ThreadLocalContext.getContext().getUsername();
    }

    protected AggregateRoot(String id, String userId) {
        requireNotBlank(id, "AR ID must not be blank.");
        requireNotBlank(userId, "User ID must not be blank.");

        this.id = id;
        this.userId = userId;
        this.createdAt = now();
        this.createdBy = ThreadLocalContext.getContext().getUid();
        this.creator = ThreadLocalContext.getContext().getUsername();
    }

    /**
     * 添加操作日志，默认无类型
     *
     * @param note 记录
     */
    protected void addOpsLog(String note) {
        addOpsLog(OpsLogTypeEnum.NONE, note);
    }

    /**
     * 添加操作日志
     *
     * @param type 操作类型
     * @param note 记录
     */
    protected void addOpsLog(OpsLogTypeEnum type, String note) {
        OpsLog log = OpsLog.builder()
                .type(type)
                .note(note)
                .optAt(now())
                .optBy(ThreadLocalContext.getContext().getUid())
                .obn(ThreadLocalContext.getContext().getUsername())
                .build();
        List<OpsLog> opsLogs = allOpsLog();

        opsLogs.add(log);
        // 最多保留最近MAX_OPS_LOG_SIZE条
        if (opsLogs.size() > MAX_OPS_LOG_SIZE) {
            this.opsLogs.remove();
        }

        this.updatedAt = now();
        this.updatedBy = ThreadLocalContext.getContext().getUid();
        this.updater = ThreadLocalContext.getContext().getUsername();
    }

    /**
     * 获取所有操作日志
     *
     * @return 操作日志集合，如果没有则返回空集合
     */
    private List<OpsLog> allOpsLog() {
        if (isNull(opsLogs)) {
            this.opsLogs = new LinkedList<>();
        }
        return opsLogs;
    }

    /**
     * 触发事件
     *
     * @param event 事件
     */
    protected void raiseEvent(DomainEvent event) {
        event.setArInfo(this);
        allEvents().add(event);
    }

    /**
     * 清除领域事件
     * 为了方便传输和持久化，需要清除领域事件给聚合根瘦身
     */
    public void clearEvents() {
        this.events = null;
    }

    /**
     * 获取所有领域事件
     *
     * @return 领域事件集合，没有就返回空集合
     */
    private List<DomainEvent> allEvents() {
        if (isNull(events)) {
            this.events = new ArrayList<>();
        }
        return events;
    }
}
