package org.ricky.common.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/9
 * @className OpsLog
 * @desc 操作日志对象
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OpsLog implements Serializable {

    /**
     * 操作时间
     */
    Instant optAt;

    /**
     * 操作人
     */
    String optBy;

    /**
     * 操作人名字
     */
    String obn;

    /**
     * 操作类型
     */
    OpsLogTypeEnum type;

    /**
     * 记录
     */
    String note;

    public Instant getOperatedAt() {
        return optAt;
    }

    public String getOperatedBy() {
        return optBy;
    }

    public String getOperatedByName() {
        return obn;
    }

}
