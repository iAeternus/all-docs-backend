package org.ricky.common.sensitiveword.domain.filter;

import java.util.List;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/8/21
 * @className SensitiveWordFilter
 * @desc 敏感词过滤
 */
public interface SensitiveWordFilter {

    /**
     * 判断文本中是否含有敏感词
     *
     * @param text 文本
     * @return true=有 false=没有
     */
    boolean hasSensitiveWord(String text);

    /**
     * 敏感词替换<br>
     * 过滤文本中的敏感词
     *
     * @param text 待替换文本
     * @return 替换后的文本
     */
    String filter(String text);

    /**
     * 加载敏感词列表
     *
     * @param words 敏感词数组
     */
    void loadWord(List<String> words);

}
