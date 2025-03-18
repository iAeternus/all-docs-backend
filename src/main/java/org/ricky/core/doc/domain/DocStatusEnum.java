package org.ricky.core.doc.domain;

import lombok.Getter;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/12
 * @className DocStatusEnum
 * @desc
 */
@Getter
public enum DocStatusEnum {

    /**
     * 建立索引时的等待状态，默认都是等待状态
     */
    WAITE("等待"),

    /**
     * 处理中的状态
     */
    ON_PROCESS("处理中"),

    /**
     * 成功状态
     */
    SUCCESS("成功"),

    /**
     * 失败状态
     */
    FAIL("失败");

    final String desc;

    DocStatusEnum(String desc) {
        this.desc = desc;
    }
}
