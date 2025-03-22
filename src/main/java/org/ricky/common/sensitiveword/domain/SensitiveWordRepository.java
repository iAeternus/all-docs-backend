package org.ricky.common.sensitiveword.domain;

import java.util.List;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/8/21
 * @className SensitiveWordRepository
 * @desc
 */
public interface SensitiveWordRepository {

    /**
     * 批量新增敏感词
     *
     * @param sensitiveWords 敏感词集合
     */
    void insertBatch(List<SensitiveWord> sensitiveWords);

    /**
     * 返回敏感词数据
     *
     * @return 敏感词数据
     */
    List<SensitiveWord> listAll();

}
