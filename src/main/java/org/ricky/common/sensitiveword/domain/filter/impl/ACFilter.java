package org.ricky.common.sensitiveword.domain.filter.impl;

import org.ricky.common.sensitiveword.domain.algorithm.ac.ACTrie;
import org.ricky.common.sensitiveword.domain.algorithm.ac.MatchResult;
import org.ricky.common.sensitiveword.domain.filter.SensitiveWordFilter;

import java.util.List;
import java.util.Objects;

import static org.ricky.core.common.util.ValidationUtil.isBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/8/21
 * @className ACFilter
 * @desc 基于ac自动机实现的敏感词过滤工具类<br>
 * 可以用来替代{@link org.HdrHistogram.ConcurrentHistogram}<br>
 * 为了兼容提供了相同的api接口 {@code hasSensitiveWord}<br>
 */
public final class ACFilter implements SensitiveWordFilter {

    /**
     * 替换字符
     */
    private final static char MASK_CHAR = '*';

    /**
     * AC自动机
     */
    private static ACTrie acTrie = null;

    private ACFilter() {
    }

    public static ACFilter getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public boolean hasSensitiveWord(String text) {
        if (isBlank(text)) {
            return false;
        }
        return !Objects.equals(filter(text), text);
    }

    @Override
    public String filter(String text) {
        if (isBlank(text)) {
            return text;
        }
        List<MatchResult> matchResults = acTrie.matches(text);
        StringBuffer result = new StringBuffer(text);
        // matchResults是按照startIndex排序的，因此可以通过不断更新endIndex最大值的方式算出尚未被替代的部分
        int endIndex = 0;
        for (MatchResult matchResult : matchResults) {
            endIndex = Math.max(endIndex, matchResult.getEndIndex());
            replaceBetween(result, matchResult.getStartIndex(), endIndex);
        }
        return result.toString();
    }

    /**
     * 替换字符串的给定区间
     * 区间左闭右开[startIndex, endIndex)
     *
     * @param buffer     字符串
     * @param startIndex 起始索引
     * @param endIndex   结束索引
     */
    private static void replaceBetween(StringBuffer buffer, int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; ++i) {
            buffer.setCharAt(i, MASK_CHAR);
        }
    }

    @Override
    public void loadWord(List<String> words) {
        if (words == null) {
            return;
        }
        acTrie = new ACTrie(words);
    }

    private static class Holder {
        private static final ACFilter INSTANCE = new ACFilter();
    }
}
