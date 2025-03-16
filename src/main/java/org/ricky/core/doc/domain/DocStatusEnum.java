package org.ricky.core.doc.domain;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/12
 * @className DocStatusEnum
 * @desc
 */
public enum DocStatusEnum {

    /**
     * 建立索引时的等待状态，默认都是等待状态
     */
    WAITE,

    /**
     * 进行中的状态
     */
    ON_PROCESS,

    /**
     * 成功状态
     */
    SUCCESS,

    /**
     * 失败状态
     */
    FAIL;

}
