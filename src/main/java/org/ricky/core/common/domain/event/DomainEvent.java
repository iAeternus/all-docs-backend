package org.ricky.core.common.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.common.context.ThreadLocalContext;
import org.ricky.core.common.domain.AggregateRoot;
import org.ricky.common.util.SnowflakeIdGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

import static lombok.AccessLevel.PROTECTED;
import static org.ricky.common.constants.ConfigConstant.EVENT_COLLECTION;
import static org.ricky.common.util.ValidationUtil.isNotBlank;
import static org.ricky.common.util.ValidationUtil.requireNonNull;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/5
 * @className DomainEvent
 * @desc 领域事件 <br>
 * 这里ar代指AggregateRoot
 * DomainEvent既要保证能支持MongoDB的序列化/反序列化，有要能够通过Jackson序列化/反序列化（因为要发送到Redis）<br>
 * @see org.ricky.common.json.JsonTypeDefine 其子类必须加@JsonTypeDefine注解才可正常序列化<br>
 * TODO
 */
@Getter
@Document(EVENT_COLLECTION)
@NoArgsConstructor(access = PROTECTED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
public abstract class DomainEvent {

    /**
     * 事件ID，不能为空
     */
    private String id;

    /**
     * 事件对应的用户ID，不能为空
     */
    private String arUserId;

    /**
     * 事件对应的聚合根ID，不能为空
     */
    private String arId;

    /**
     * 事件类型
     */
    private DomainEventTypeEnum type;

    /**
     * 事件状态
     */
    private DomainEventStatusEnum status;

    /**
     * 已经发布的次数，无论成功与否
     */
    private int publishedCount;

    /**
     * 已经被消费的次数，无论成功与否
     */
    private int consumedCount;

    /**
     * 引发该事件的memberId
     */
    private String raisedBy;

    /**
     * 事件产生时间
     */
    private Instant raisedAt;

    protected DomainEvent(DomainEventTypeEnum type) {
        requireNonNull(type, "Domain event type must not be null.");

        this.id = newEventId();
        this.type = type;
        this.status = DomainEventStatusEnum.CREATED;
        this.publishedCount = 0;
        this.consumedCount = 0;
        this.raisedBy = ThreadLocalContext.getContext().getUid();
        this.raisedAt = Instant.now();
    }

    /**
     * 生成事件id
     *
     * @return 事件id
     */
    public String newEventId() {
        return "EVT" + SnowflakeIdGenerator.newSnowflakeId();
    }

    /**
     * 设置聚合根信息
     *
     * @param ar 聚合根
     */
    public void setArInfo(AggregateRoot ar) {
        this.arUserId = ar.getUserId();
        this.arId = ar.getId();
    }

    /**
     * 判断该事件是否已被消费
     *
     * @return true=已被消费 false=未被消费
     */
    public boolean isConsumeBefore() {
        return this.consumedCount > 0;
    }

    /**
     * 判断该事件是否未被消费
     *
     * @return true=未被消费 false=已被消费
     */
    public boolean isNotConsumedBefore() {
        return !isConsumeBefore();
    }

    /**
     * 判断该事件是否由人类用户发出
     *
     * @return true=是 false=否
     */
    public boolean isRaisedByHuman() {
        return isNotBlank(raisedBy);
    }

}
