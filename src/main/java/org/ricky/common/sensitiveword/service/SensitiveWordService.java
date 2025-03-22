package org.ricky.common.sensitiveword.service;


import org.ricky.common.sensitiveword.domain.SensitiveWord;
import org.ricky.common.sensitiveword.domain.SensitiveWordRepository;
import org.ricky.common.sensitiveword.domain.filter.SensitiveWordFilter;
import org.ricky.common.sensitiveword.domain.filter.impl.DFAFilter;

import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/8/21
 * @className SensitiveWordService
 * @desc 敏感词引导类
 */
public class SensitiveWordService {

    /**
     * 脱敏策略
     */
    private SensitiveWordFilter sensitiveWordFilter = DFAFilter.getInstance();

    /**
     * 敏感词列表
     */
    private SensitiveWordRepository sensitiveWordRepository;

    private SensitiveWordService() {
    }

    public static SensitiveWordService newInstance() {
        return new SensitiveWordService();
    }

    /**
     * 初始化<br>
     * 根据配置，初始化对应的 map。比较消耗性能。
     *
     * @return 返回自身，便于链式编程
     */
    public SensitiveWordService init() {
        List<String> wordList = sensitiveWordRepository.listAll().stream()
                .map(SensitiveWord::getWord)
                .collect(toImmutableList());
        loadWords(wordList);
        return this;
    }

    /**
     * 加载敏感词列表
     *
     * @param wordList 敏感词数组
     */
    private void loadWords(List<String> wordList) {
        sensitiveWordFilter.loadWord(wordList);
    }

    /**
     * 设置过滤策略
     *
     * @param filter 过滤器
     * @return 返回自身，便于链式编程
     */
    public SensitiveWordService filterStrategy(SensitiveWordFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("filter can not be null");
        }
        this.sensitiveWordFilter = filter;
        return this;
    }

    /**
     * 设置敏感词列表持久化策略
     *
     * @param sensitiveWordRepository 仓储接口
     * @return 返回自身，便于链式编程
     */
    public SensitiveWordService sensitiveWord(SensitiveWordRepository sensitiveWordRepository) {
        if (sensitiveWordRepository == null) {
            throw new IllegalArgumentException("wordFactory can not be null");
        }
        this.sensitiveWordRepository = sensitiveWordRepository;
        return this;
    }

    /**
     * 判断文本中是否含有敏感词
     *
     * @param text 文本
     * @return true=有 false=没有
     */
    public boolean hasSensitiveWord(String text) {
        return sensitiveWordFilter.hasSensitiveWord(text);
    }

    /**
     * 过滤文本中的敏感词
     *
     * @param text 文本
     * @return 返回过滤后的文本
     */
    public String filter(String text) {
        return sensitiveWordFilter.filter(text);
    }

}
