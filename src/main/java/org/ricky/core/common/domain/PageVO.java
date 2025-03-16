package org.ricky.core.common.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/12
 * @className PageVO
 * @desc 分页查询响应体
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PageVO<T> implements VO {

    /**
     * 总数量
     */
    Integer totalCnt;

    /**
     * 页号
     */
    Integer pageNum;

    /**
     * 每页条数
     */
    Integer pageSize;

    /**
     * 数据
     */
    List<T> data;

}