package org.ricky.common.sensitiveword.domain.algorithm.ac;

import lombok.Data;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/8/21
 * @className MatchResult
 * @desc 敏感词匹配的结果
 */
@Data
public class MatchResult {

    /**
     * 起始索引
     */
    private int startIndex;

    /**
     * 结束索引
     */
    private int endIndex;

    public MatchResult(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public String toString() {
        return "MatchResult{" +
                "startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                '}';
    }
}
