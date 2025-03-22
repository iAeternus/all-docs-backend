package org.ricky.common.sensitiveword.domain.filter.impl;

import org.ricky.common.sensitiveword.domain.algorithm.acpro.ACProTrie;
import org.ricky.common.sensitiveword.domain.filter.SensitiveWordFilter;

import java.util.List;
import java.util.Objects;

import static org.ricky.core.common.util.ValidationUtil.isBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/8/21
 * @className ACProFilter
 * @desc 基于ACFilter的优化增强版本
 */
public final class ACProFilter implements SensitiveWordFilter {

    /**
     * AC自动机
     */
    private ACProTrie acProTrie;

    private ACProFilter() {
    }

    public static ACProFilter getInstance() {
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
        return acProTrie.match(text);
    }

    @Override
    public void loadWord(List<String> words) {
        if (words == null) {
            return;
        }
        acProTrie = new ACProTrie();
        acProTrie.createACTrie(words);
    }

    private static class Holder {
        private static final ACProFilter INSTANCE = new ACProFilter();
    }
}
